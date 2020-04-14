package com.cxytiandi.encrypt.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cxytiandi.encrypt.util.StreamUtils;

public class EncryptionReqestWrapper extends HttpServletRequestWrapper  {

	private byte[] requestBody;
    private JSONObject jsonBody;

	public EncryptionReqestWrapper(HttpServletRequest request) {
		super(request);
		try {
			requestBody = StreamUtils.copyToByteArray(request.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    @Override
    public String getParameter(String name) {
        if (null != this.jsonBody) {
            Object obj = this.jsonBody.getObj(name);
            if (null != obj) {
                return String.valueOf(obj);
            }
        }
        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (null != this.jsonBody) {
            Object obj = this.jsonBody.getObj(name);
            if (null != obj) {
                return new String[]{String.valueOf(obj)};
            }
        }
        return super.getParameterValues(name);
    }

	@Override
	public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }
 
            @Override
            public boolean isFinished() {
                return false;
            }
 
            @Override
            public boolean isReady() {
                return true;
            }
 
            @Override
            public void setReadListener(ReadListener listener) {
 
            }
        };
	}

	public String getRequestData() {
		return new String(requestBody);
	}

    public void setRequestData(String requestData) {
        this.requestBody = requestData.getBytes();
        // json格式解析
        if (null != requestData && requestData.startsWith("{")) {
            this.jsonBody = JSONUtil.parseObj(requestData);
        }
    }
}
