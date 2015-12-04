# happor
A netty-spring-based web controller framework.

I consider that Netty bound with SpringMVC could be a very nice web MVC framework instead of Tomcat + Servlet + SpringMVC. 

However, SpringMVC is based on Servlet and, it's hard to dettach Servlet from SpringMVC. In some implements, we may only focus on controller usage, such as RESTful API develop. 

Things can be simplification. By using Spring IoC to make a controller framework based on Netty without Servlet, I setup this project.

# usage
Setup beans(webserver, controllers) in web.xml, and write codes to run server in Test.java. This project is easy to understand.

目录docs下有中文文档。
