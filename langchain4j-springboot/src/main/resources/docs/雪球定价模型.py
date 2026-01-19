##########导入库##########
import numpy as np
import datetime as dt
from functools import lru_cache
import requests

##########定价参数##########
s0 = 6335.0                            # 交易确认书上的期初价格（这个期初价格会影响敲入敲出价格、结算金额）
s0_now= 6324.0                         # 标的资产初始价格（根据行情确定），作为蒙特卡罗路径的初始价格
nominal=10000000                       # 名义本金
deposit=2500000                        # 保证金
knockin=0.75                           # 敲入价格比例
knockout=1.0                           # 敲出价格比例
knock_in_price=round(knockin*s0,2)     # 敲入价格=交易确认书上的期初价格*敲入价格比例，精确到0.01
knock_out_price=round(knockout*s0,2)   # 敲出价格=交易确认书上的期初价格*敲出价格比例，精确到0.01
begin_date='2022-03-31'                # 开始日期
val_date='2022-03-31'                  # 估值日期

#敲出观察日
KnockOutDate='2022-06-29,2022-07-29,2022-08-30,2022-09-29,2022-10-28,2022-11-29,2022-12-30,2023-01-30,2023-02-27,2023-03-30,2023-04-28,2023-05-30,2023-06-29,2023-07-28,2023-08-30,2023-09-28,2023-10-30,2023-11-29,2023-12-29,2024-01-30,2024-02-28,2024-03-29'
pay_Coupon=0                          # 返息率（后端收益率(年化)），卖方根据合约存续天数，以名义本金和实际存续天数为基础，按返息率计息支付给买方
knockout_Coupon=0.11                  # 敲出收益率/红利票息率（敲出收益率是提前敲出时使用的计息利率，红利票息率是未敲出也未敲入使用的计息利率，在雪球结构中一般相等）
ratio=0.75                            # 保底比例，即投资者承担的最大损失=名义本金×（1-保底比例）
r = 0.024                             # 无风险利率（年化）
q = 0.1                               # 期货贴水率（年化）
sigma = 0.2287                        # 标的波动率（年化)
numpath=100000                        # 模拟价格路径的条数，路径次数少一点速度更快。
status= 0                             # status表示雪球状态：0是当前未敲入，非0是已敲入
T_trading=245                         # 一年的交易日天数

##########定义日期计算函数##########
@lru_cache() #加上访问缓存，减少对服务器的访问
def get_trade_date_lst():
    # 获取最新交易日历
    r=requests.get(url='http://121.5.174.69/tradedayslst')
    trade_date_lst=r.json()['data']
    return trade_date_lst
    
def get_delta_trade_days(begin,end):
    ''' 计算交易日间隔
    begin/end:格式是yyyy-mm-dd。
    '''
    trade_date_lst=get_trade_date_lst()
    
    if begin in trade_date_lst and  end in trade_date_lst:
        #print('1',trade_date_lst.index(end)-trade_date_lst.index(begin))
        return trade_date_lst.index(end)-trade_date_lst.index(begin)
    else:
        if not begin in trade_date_lst:
            for i in range(20):
                begin_new=dt.datetime.strftime(dt.datetime.strptime(begin,"%Y-%m-%d")-dt.timedelta(days=i),"%Y-%m-%d") #如果begin是假期，减去一天直到是交易日。
                if begin_new in trade_date_lst:
                    begin=begin_new
                    break
            
        if not end in trade_date_lst:
            for i in range(20):
                end_new=dt.datetime.strftime(dt.datetime.strptime(end,"%Y-%m-%d")-dt.timedelta(days=i),"%Y-%m-%d") #如果end是假期，减去一天直到是交易日。
                if end_new in trade_date_lst:
                    end=end_new
                    break
        
        #print('2',trade_date_lst.index(end)-trade_date_lst.index(begin))
        return trade_date_lst.index(end)-trade_date_lst.index(begin)

def GetActualDaysBetweenTwoDate(begin,end):
    ''' 获取实际天数间隔
    begin/end:格式是yyyy-mm-dd。
    '''
    begin1=dt.datetime.strptime(begin,'%Y-%m-%d')
    end1=dt.datetime.strptime(end,'%Y-%m-%d')
    return (end1-begin1).days

##########计算日期数据##########
#计算KnockOutTradeStep，列表格式，估值日（val_date）到未来每个敲出观察日的交易日间隔天数，需要过滤掉已经pass的敲出观察日（若今天是观察日也过滤）。
KnockOutTradeStep=[]
KnockOutDate_lst=KnockOutDate.split(',')
KnockOutDate_lst.sort(reverse=False) #防止敲出观察日不按顺序排列
for date in KnockOutDate_lst:
    trade_days_between=get_delta_trade_days(val_date,date)
    #print(val_date,date,trade_days_between) 
    if trade_days_between>0: #过滤掉估值日以前的敲出观察日
        KnockOutTradeStep.append(trade_days_between)

#计算KnockOutActualStep，列表格式，开始日期（begin_date）到未来每个敲出观察日的实际天数间隔，需要过滤掉已经pass的敲出观察日（若今天是观察日也过滤）。
KnockOutActualStep=[]
for date in KnockOutDate_lst:
    actual_days_between=GetActualDaysBetweenTwoDate(begin_date,date)
    #print(begin_date,date,actual_days_between)
    KnockOutActualStep.append(actual_days_between)
    #长度修正：需要和KnockOutTradeStep保持一致。
    KnockOutActualStep=KnockOutActualStep[-len(KnockOutTradeStep):] 


##########采用向量化方法生成蒙特卡罗路径期数据##########
#设置种子
np.random.seed(105)
#Residualdays:估值日期到最后一个敲出观察日（一般也是结束日）一共多少个交易日
Residualdays=KnockOutTradeStep[-1] #或者get_delta_trade_days(val_date,KnockOutDate_lst[-1])
ResidualYears=Residualdays/T_trading
n=1*Residualdays #n表示MC步数，锁定为剩余多少天就走几步
#通过np.random.standard_normal(模拟路径条数)生成多个标准正态随机数(记得设置seed)。
knock_out_keeper=np.zeros(numpath) #用于记录敲出路径的索引，初始值为0.
acc_knock_out_times=0 #累计敲出笔数
if status==0:#未敲入
    knock_in_keeper=np.zeros(numpath) #用于记录敲入路径的索引。
else:#已敲入
    knock_in_keeper=np.ones(numpath) 
s_next=np.ones(numpath)*s0_now #期初价格向量
acc_income=0 #累计收益
for i in range(1,Residualdays+1): #第i个价格就是第i个敲入观察日的收盘价格。这里的i加上1确保含义一致。
    z=np.random.standard_normal(numpath)
    #print(z)
    s_next=s_next*np.exp((r-q-0.5*np.square(sigma))*ResidualYears/n+sigma*np.sqrt(ResidualYears/n)*z)
    #print(s_next)
    # 如果i是敲出观察日对应的步数，则要计算本步有几笔敲出
    if i in KnockOutTradeStep:
        #敲出统计
        knock_out_keeper+=(s_next>=knock_out_price)
        #print("敲出路径情况",i,knock_out_keeper,s_next)
        current_knock_out=np.sum(knock_out_keeper!=0) -acc_knock_out_times
        acc_knock_out_times+=current_knock_out
        #print(f"当前是MC的第{i}步需要进行敲出观察,本次敲出{current_knock_out}笔，累计敲出{acc_knock_out_times}笔")
        cost_total=(deposit*np.exp(r*i/T_trading)-deposit)*current_knock_out #i是估值日到敲出观察日对应的步数，由于是一天一步，i/T_trading也表示合约提前敲出的持续年限
        #print('所有路径的在当前步上的保证金成本',cost_total)
        #计算本步敲出收益
        amount_total=nominal*KnockOutActualStep[KnockOutTradeStep.index(i)]/365*(knockout_Coupon+pay_Coupon)*current_knock_out-cost_total
        #print('计息公式：',amount_total+cost_total,nominal,KnockOutActualStep[KnockOutTradeStep.index(i)],knockout_Coupon+pay_Coupon,current_knock_out)
        discount=np.exp(-r*i/T_trading)
        acc_income+=amount_total*discount
    # 敲入统计
    knock_in_keeper+=(s_next<knock_in_price)
    #print("敲入统计",i,knock_in_keeper,s_next)

#处理未敲出（knock_out_keeper为0的位置）且未敲入（knock_in_keeper为0的位置）
lst1=s_next[(knock_out_keeper==0)&(knock_in_keeper==0)] 
if len(lst1)!=0: #计算有几笔属于未敲出且未敲入
    cost_total1=(deposit*np.exp(r *ResidualYears)-deposit)*len(lst1)
    abs_amount=nominal*KnockOutActualStep[-1]/365*(knockout_Coupon+pay_Coupon)*len(lst1)-cost_total1
    discount=np.exp(-r*ResidualYears)  
    acc_income+=abs_amount*discount

#处理未敲出（knock_out_keeper为0的位置）且已敲入（knock_in_keeper不为0的位置）
lst2=s_next[(knock_out_keeper==0)&(knock_in_keeper!=0)]
if len(lst2)!=0: #计算有几笔属于未敲出且已敲入
    cost2=(deposit*np.exp(r *ResidualYears)-deposit) #这里不要乘以len(lst2)，因为下面方法会np.sum()，相当于乘以len(lst2)
    abs_amount=np.sum(nominal*(-np.minimum(1-ratio, np.maximum(0, 1-lst2/s0)) + pay_Coupon*KnockOutActualStep[-1]/365)-cost2)
    acc_income+=abs_amount*discount

#投资者的PV(雪球期权的买方PV)，单位元。
print("投资者的PV（元）：",acc_income/numpath)