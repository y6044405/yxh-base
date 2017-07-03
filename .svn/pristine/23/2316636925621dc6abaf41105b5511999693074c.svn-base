package com.tzg.ex.mvc.web.view.velocity.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.tzg.ex.mvc.web.core.Constants;
import com.tzg.ex.mvc.web.util.CSRFUtil;
import com.tzg.ex.mvc.web.util.SpringUtils;

public class TokenCodeDirective extends Directive {
    private static VelocityEngine velocityEngine;

    @Override
    public String getName() {
        return "tokenCode";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String name = Constants.CSRF_PARAM_NAME;
        ViewToolContext vtc = (ViewToolContext) context.getInternalUserContext();
        String value = CSRFUtil.getTokenForSession(vtc.getRequest().getSession());
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", name);
            map.put("value", value);
            Template template = getVelocityEngine().getTemplate("include/token.htm", "UTF-8");
            StringWriter sResult = new StringWriter();
            template.merge(new VelocityContext(map), sResult);
            writer.write(sResult.toString());
        } catch (Exception e) {
            writer.write(MessageFormat.format("<input type='hidden' name=\"{0}\" value=\"{1}\" >", name, value));
        }
        return true;
    }

    public String getParameter(InternalContextAdapter context, Node node, int index) throws ParseErrorException {
        SimpleNode sn_value = (SimpleNode) node.jjtGetChild(index);
        if (sn_value == null) {
            return "";
        }
        String value = (String) sn_value.value(context);
        return value == null ? "" : value;
    }

    public static VelocityEngine getVelocityEngine() {
        if (null == velocityEngine) {
            velocityEngine = SpringUtils.getBean(VelocityConfigurer.class).getVelocityEngine();
        }
        return velocityEngine;
    }

    public static void setVelocityEngine(VelocityEngine velocityEngine) {
        TokenCodeDirective.velocityEngine = velocityEngine;
    }

}