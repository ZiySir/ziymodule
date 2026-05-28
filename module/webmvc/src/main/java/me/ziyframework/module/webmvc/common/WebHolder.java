package me.ziyframework.module.webmvc.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.ziyframework.boot.core.SpringHolder;
import me.ziyframework.framework.Lazy;
import me.ziyframework.module.webmvc.common.dto.Result;
import me.ziyframework.module.webmvc.utils.JsonUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * web holder类.
 *
 * @author ziy
 */
public final class WebHolder {

    private static final Lazy<List<HandlerMapping>> HANDLER_MAPPING_LAZY = Lazy.of(() -> {
        DispatcherServlet dispatcherServlet = getDispatcherServlet();
        return Preconditions.checkNotNull(
                dispatcherServlet.getHandlerMappings(), "dispatcherServlet的getHandlerMappings方法返回null");
    });

    private WebHolder() {}

    /**
     * 获取当前线程的HttpServletRequest.
     *
     * <p>注意：此方法必须在Web请求线程中调用，否则会抛出NullPointerException
     *
     * @return 当前请求的HttpServletRequest对象
     * @throws NullPointerException 如果不在Web主线程上调用或无法获取到Request
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest =
                (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (httpServletRequest == null) {
            throw new NullPointerException("无法获取Request,请检查是否在web主线程上调用");
        }
        return httpServletRequest;
    }

    /**
     * 获取当前线程的HttpServletResponse.
     *
     * <p>注意：此方法必须在Web请求线程中调用，否则会抛出NullPointerException
     *
     * @return 当前请求的HttpServletResponse对象
     * @throws NullPointerException 如果不在Web主线程上调用或无法获取到Response
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Preconditions.checkNotNull(servletRequestAttributes, "ServletRequestAttributes must not be null");
        HttpServletResponse response = servletRequestAttributes.getResponse();
        if (response == null) {
            throw new NullPointerException("无法获取Request,请检查是否在web主线程上调用");
        }
        return response;
    }

    /**
     * 获取当前Servlet环境的OutputStream.
     *
     * <p>用于直接写入二进制数据到响应流
     *
     * @return ServletOutputStream对象
     * @throws RuntimeException 如果获取OutputStream失败
     */
    public static ServletOutputStream getOutputStream() {
        HttpServletResponse response = getResponse();
        try {
            return response.getOutputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 将对象序列化为JSON并写入响应流.
     *
     * <p>此方法会自动获取OutputStream并将对象转换为UTF-8编码的JSON字节写入
     *
     * @param object 要写入的对象，会通过JsonUtil序列化为JSON
     * @throws RuntimeException 如果写入失败
     */
    public static void write(Object object) {
        try {
            getOutputStream().write(JsonUtil.toJsonSpring(object).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 判断当前线程是否处于Web环境.
     *
     * @return 如果当前线程有RequestAttributes则返回true，否则返回false
     */
    public static boolean isWeb() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    /**
     * 获取当前请求的HTTP方法.
     *
     * @return HTTP方法枚举，如GET、POST、PUT、DELETE等
     * @see HttpMethod
     */
    public static HttpMethod getMethod() {
        return HttpMethod.valueOf(getRequest().getMethod());
    }

    /**
     * 获取当前请求的路径部分.
     *
     * <p>返回请求的URI路径部分，不包含协议、域名和查询参数
     *
     * @return 请求路径，例如 /user/profile
     */
    public static String getPath() {
        return getRequest().getRequestURI();
    }

    /**
     * 重置响应内容并返回指定result的JSON字符串，HTTP状态码默认为200 OK.
     *
     * @param result 要返回的结果对象，会被序列化为JSON
     */
    public static void resetResponse(Result<?> result) {
        resetResponse(HttpStatus.OK, result);
    }

    /**
     * 重置响应内容并返回指定result的JSON字符串.
     *
     * <p>此方法会执行以下操作：
     * <ol>
     *   <li>清空响应缓冲区和已写入的内容</li>
     *   <li>设置指定的HTTP状态码</li>
     *   <li>设置Content-Type为application/json</li>
     *   <li>设置字符编码为UTF-8</li>
     *   <li>写入序列化后的JSON数据</li>
     * </ol>
     *
     * @param status HTTP状态码
     * @param result 要返回的结果对象，会被序列化为JSON
     * @throws RuntimeException 如果写入失败
     */
    public static void resetResponse(HttpStatus status, Result<?> result) {
        HttpServletResponse response = getResponse();
        response.reset();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JsonUtil.toJsonSpring(result));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 获取指定名称的请求头.
     *
     * @param headerName 请求头的名称
     * @return 请求头的值，如果不存在该请求头则返回null
     */
    public static @Nullable String getRequestHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }

    /**
     * 批量获取指定的请求头.
     *
     * <p>根据传入的请求头名称数组，返回对应的请求头值映射
     *
     * @param headers 请求头名称数组
     * @return 请求头名称到值的映射Map，如果headers为空则返回空Map
     */
    public static Map<String, String> getRequestHeader(String... headers) {
        if (headers.length == 0) {
            return Collections.emptyMap();
        }
        HttpServletRequest request = getRequest();
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builderWithExpectedSize(headers.length);
        for (String header : headers) {
            builder.put(header, request.getHeader(header));
        }
        return builder.buildKeepingLast();
    }

    /**
     * 获取单个上传文件.
     *
     * <p>获取指定名称的文件，仅适用于单文件上传场景
     *
     * @param name 表单字段名称
     * @return MultipartFile对象，如果不存在则返回null
     * @throws IllegalArgumentException 如果当前请求不是MultipartHttpServletRequest类型
     * @see #getFiles(String)
     */
    public static @Nullable MultipartFile getFile(String name) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartHttpServletRequest multipartHttpServletRequest) {
            return multipartHttpServletRequest.getFile(name);
        }
        throw new IllegalArgumentException("type not MultipartHttpServletRequest");
    }

    /**
     * 获取上传的文件列表.
     *
     * <p>用于获取指定表单字段名称的所有文件，支持多文件上传场景。
     * 当配置了spring.servlet.multipart.resolve-lazily: true时，可以通过该方法配合实现延迟获取文件对象
     *
     * @param name 表单字段名称
     * @return 文件列表，如果不存在则返回空列表
     * @throws IllegalArgumentException 如果当前请求不是MultipartHttpServletRequest类型
     */
    public static List<MultipartFile> getFiles(String name) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartHttpServletRequest multipartHttpServletRequest) {
            return multipartHttpServletRequest.getFiles(name);
        }
        throw new IllegalArgumentException("type not MultipartHttpServletRequest");
    }

    /**
     * 返回已配置的HandlerMapping列表.
     *
     * <p>HandlerMapping用于将请求路由到对应的Handler（Controller）
     *
     * @return 系统中所有已注册的HandlerMapping列表
     */
    public static List<HandlerMapping> getHandlerMapping() {
        return HANDLER_MAPPING_LAZY.get();
    }

    /**
     * 获取当前请求的Handler执行链.
     *
     * <p>遍历所有的HandlerMapping，找到能够处理当前请求的Handler并返回其执行链
     *
     * @param request HttpServletRequest对象
     * @return HandlerExecutionChain执行链，如果找不到对应的Handler则返回null
     * @throws RuntimeException 如果在获取Handler过程中发生异常
     */
    public static @Nullable HandlerExecutionChain getHandler(HttpServletRequest request) {
        List<HandlerMapping> handlerMappings = HANDLER_MAPPING_LAZY.get();
        for (HandlerMapping mapping : handlerMappings) {
            HandlerExecutionChain handler;
            try {
                handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * 获取当前请求的HandlerMethod.
     *
     * <p>从HandlerExecutionChain中提取HandlerMethod，这是Spring MVC中代表Controller方法的封装
     *
     * @param request HttpServletRequest对象
     * @return 当前请求对应的HandlerMethod
     * @throws RuntimeException 如果无法获取到HandlerMethod（找不到Handler或Handler不是HandlerMethod类型）
     */
    public static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        HandlerExecutionChain chain = getHandler(request);
        if (chain == null) {
            throw new RuntimeException("无法获取HandlerMethod");
        }
        Object handler = chain.getHandler();
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod;
        }
        throw new RuntimeException("无法获取HandlerMethod");
    }

    /**
     * 获取DispatcherServlet实例.
     *
     * <p>从Spring容器中获取DispatcherServlet Bean
     *
     * @return DispatcherServlet实例
     */
    private static DispatcherServlet getDispatcherServlet() {
        return SpringHolder.getBean(DispatcherServlet.class);
    }
}
