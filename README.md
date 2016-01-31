# happor
A netty-spring-based web controller framework.

I consider that Netty bound with SpringMVC could be a very nice web MVC framework instead of Tomcat + Servlet + SpringMVC. 

However, SpringMVC is based on Servlet and, it's hard to dettach Servlet from SpringMVC. In some implements, we may only focus on controller usage, such as RESTful API develop. 

Things can be simplification. By using Spring IoC to make a controller framework based on Netty without Servlet, I setup this project.

#A hello-world demo
```Java
@Controller(method="GET", uriPattern="^/test/(\\w+)")
public class Test extends HttpNormalHandler {
	
	@UriSection(1)
	private String name;

	@Override
	protected void handle(FullHttpRequest request, FullHttpResponse response) {
		// TODO Auto-generated method stub
		String words = "hello " + name;
		response.content().writeBytes(words.getBytes());
		response.headers().set("Content-Type", "text/plain");
		response.headers().set("Content-Length", response.content().readableBytes());
	}

	@Override
	protected void atlast() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HapporAutomaticContext context = new HapporAutomaticContext();
		context.runServer();
	}

}
```

Run and visit `http://localhost/test/someone`, the browser will show `hello someone`.
