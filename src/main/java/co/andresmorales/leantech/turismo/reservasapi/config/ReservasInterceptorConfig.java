package co.andresmorales.leantech.turismo.reservasapi.config;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ReservasInterceptorConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ReservasHandlerInterceptor());
	}

	private class ReservasHandlerInterceptor implements HandlerInterceptor {

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			Enumeration<String> headers = request.getHeaderNames();
			Logger log = LoggerFactory.getLogger(ReservasHandlerInterceptor.class);
			StringBuilder builder = new StringBuilder("Headers Peticion: {\n");
			while(headers.hasMoreElements()) {
				String header = headers.nextElement();
				String valor = request.getHeader(header);
				builder.append(header).append("=").append(valor).append("\n");
			}
			builder.append("}");
			String headersString = builder.toString();
			log.info(headersString);
			return true;
		}
	}
}
