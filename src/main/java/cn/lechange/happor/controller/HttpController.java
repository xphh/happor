package cn.lechange.happor.controller;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.annotation.Controller;
import cn.lechange.happor.annotation.UriParam;
import cn.lechange.happor.annotation.UriSection;
import cn.lechange.happor.utils.UriParser;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpController {

	private static Logger logger = Logger.getLogger(HttpController.class);

	private String method;
	private String uriPattern;
	private UriParser uriParser;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUriPattern() {
		return uriPattern;
	}

	public void setUriPattern(String uriPattern) {
		this.uriPattern = uriPattern;
	}
	
	public UriParser getUriParser() {
		return uriParser;
	}

	public void setUriParser(UriParser uriParser) {
		this.uriParser = uriParser;
	}

	private ChannelHandlerContext ctx;
	private FullHttpRequest request;
	
	public HttpController() {
		parseClassAnnotation();
	}
	
	final public boolean input(ChannelHandlerContext ctx, FullHttpRequest request,
			FullHttpResponse response) {
		logger.info("HTTP[" + request.getMethod() + " "
				+ request.getUri() + " " + request.getProtocolVersion()
				+ "] => " + this + " [ from " + prev + " ]");
		this.ctx = ctx;
		this.request = request;
		parseFieldAnnotation();
		return handleRequest(request, response);
	}
	
	final public void output(FullHttpResponse response) {
		handleResponse(response);
	}
	
	private HttpController prev;
	final public void setPrev(HttpController controller) {
		prev = controller;
	}
	
	private HapporWebserver server;
	public void setServer(HapporWebserver server) {
		this.server = server;
	}
	public HapporWebserver getServer() {
		return server;
	}
	
	private void realFinish(FullHttpResponse response) {
		if (response != null) {
			logger.info("HTTP[" + request.getMethod() + " " + request.getUri()
					+ " " + request.getProtocolVersion() + "] response " + response.getStatus());
			ctx.channel().writeAndFlush(response)
					.addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.channel().close();
		}
		request.release();
	}

	protected void finish(FullHttpResponse response) {
		handleResponse(response);
		if (prev == null) {
			realFinish(response);
		} else {
			prev.finish(response);
		}
	}

	protected abstract boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response);
	protected abstract void handleResponse(FullHttpResponse response);
	
	private void parseClassAnnotation() {
		if (getClass().isAnnotationPresent(Controller.class)) {
			Controller anno = getClass().getAnnotation(Controller.class);
			setMethod(anno.method());
			setUriPattern(anno.uriPattern());
		}
	}

	private void parseFieldAnnotation() {
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(UriSection.class)) {
				UriSection uriSection = (UriSection) field.getAnnotation(UriSection.class);
				setField(field, uriParser.getSection(uriSection.value()));
			} else if (field.isAnnotationPresent(UriParam.class)) {
				UriParam uriParam = (UriParam) field.getAnnotation(UriParam.class);
				setField(field, uriParser.getParam(uriParam.value()));
			}
		}
	}
	
	private void setField(Field field, String value) {
		field.setAccessible(true);
		try {
			if (field.getType() == int.class) {
				field.setInt(this, Integer.valueOf(value));
			} else if (field.getType() == long.class) {
				field.setLong(this, Long.valueOf(value));
			} else if (field.getType() == short.class) {
				field.setShort(this, Short.valueOf(value));
			} else if (field.getType() == float.class) {
				field.setFloat(this, Float.valueOf(value));
			} else if (field.getType() == double.class) {
				field.setDouble(this, Double.valueOf(value));
			} else {
				field.set(this, value);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
}
