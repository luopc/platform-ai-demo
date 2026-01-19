> 编译并安装 pgvector 扩展
>> 步骤 1：下载 pgvector 源码（选择适配 PG18 的版本）
>> pgvector 0.7.0 及以上版本支持 PostgreSQL 18，优先下载最新稳定版：
```java
# 克隆pgvector仓库（或直接下载源码包）
git clone --branch v0.7.0 https://github.com/pgvector/pgvector.git
cd pgvector
```

>> 步骤 2：配置编译环境（关联你的 PG 18.1）
>> 通过PG_CONFIG环境变量指定源码安装的 PG 的pg_config路径，确保编译时关联正确的 PG 版本：
```shell
# 替换为你的pg_config实际路径（关键！）
export PG_CONFIG=/usr/local/pgsql/bin/pg_config

# 配置编译（检查依赖并生成Makefile）
make USE_PGXS=1
```
>> 步骤 3：编译并安装pgvector
```shell
# 安装到PostgreSQL的扩展目录
sudo make USE_PGXS=1 install

# 若你的PG安装目录有自定义权限（比如非root安装），去掉sudo，直接执行：
# make USE_PGXS=1 install
```
>> 四、验证 pgvector 安装并启用 
>> 步骤 1：进入 PostgreSQL 18.1 的命令行
```shell
# 切换到PG安装用户（源码安装默认是postgres用户，若你自定义了用户则替换）
sudo su - postgres

# 进入PG命令行（指定你的PG安装路径的psql）
/usr/local/pgsql/bin/psql -d interview_guide  # 连接你的业务数据库
```
>> 步骤 2：启用 pgvector 扩展
```shell
-- 确认扩展目录包含pgvector（可选，验证安装路径）
SHOW shared_preload_libraries;
SHOW extension_dir;  -- 应能看到pgvector的so文件（比如vector.so）

-- 启用pgvector扩展（必须在目标数据库执行）
CREATE EXTENSION IF NOT EXISTS vector;

-- 验证扩展是否安装成功
SELECT * FROM pg_extension WHERE extname = 'vector';
```
