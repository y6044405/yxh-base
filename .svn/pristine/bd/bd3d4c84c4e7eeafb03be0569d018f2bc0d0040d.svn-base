4.8、自定义集成velocity,支持：
	
- 多配置文件支持:
	- 支持从当前class目录、jar包中读取配置文件(自定义属性configLocations值classpath*:conf/*velocity.properties),
	- 组合规则:多配置文件是组合关系,配置有重复项时，后加载的覆盖先前加载的配置项. 
	- 排序规则:先根据文件名,文件长度即jar包中的配置文件先加载,工程里的配置文件后加载
- 配置优化：
	- 增加公共配置
	- 配置优化：过期配置更新、性能、安全优化
	- 日志优化，使用统一的日志门面slf4j,桥接第三方日志log4j、jcl(commons-logging)、velocity日志、dubbo日志到slf4j(logback)、集成velocity日志到应用日志中 
具体配置参考base-spring.xml、base-velocity.properties

4.22、加入servlet3.0支持、集成公共配置

4.23、自定义集成logback,集成/桥接第三方日志组件

	
6.3、补充日志说明：

- 日志级别及文件：

	- 日志记录采用分级记录，级别与日志文件名相对应，不同级别的日志信息记录到不同的日志文件中
        
		- 例如：error级别记录到*_error_xxx.log或*_error.log（该文件为当前记录的日志文件），而*_error_xxx.log为归档日志，
        日志文件按日期记录，同一天内，若日志文件大小等于或大于10M，则按0、1、2...顺序分别命名
        例如log-level-2013-12-21.0.log
        
	- 其它级别的日志也是如此。	
- 日志文件路径
        
	- 如开发环境,在eclipse中运行测试用例(普通main方法)，默认在当前工程目录下的./logs。
	- 其他环境:测试、生产、以及开发环境web容器(jetty插件、tomcat插件、maven的jetty/tomcat插件)日志文件的路径:
		- 在全局的配置文件base-conf.properties中的log.root.dir指定日志根目录,子目录为logs/应用名/,应用名重用应用web.xml中已有的log4j配置:webAppRootKey的值，这个配置非必须配置(支持不配置):
			- 可以从应用目录下的conf/conf.properties自定义配置文件中配置
			- 如自定义配置文件缺失，支持从应用的文件路径解析.
- Appender
        
	- common-error对应error级别，文件名以应用名_error-xxx.log形式命名
	- common-default对应info级别，文件名以应用名_default-xxx.log形式命名
   	- stdout将日志信息输出到控制上，为方便开发测试使用
