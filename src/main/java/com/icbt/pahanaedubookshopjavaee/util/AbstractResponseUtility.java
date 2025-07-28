package com.icbt.pahanaedubookshopjavaee.util;

import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AbstractResponseUtility {

    /**
     * This method is used to compile the final common response as a JSON
     *
     * @param response
     * @param data
     * @throws IOException
     */
    public void writeJson(HttpServletResponse response, JsonObject data) throws IOException {
        response.setContentType(CommonConstants.MIME_TYPE_JSON);
        response.getWriter().print(data.toString());
    }
}
