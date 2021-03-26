package com.wilson.controller;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.json.XML;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/soap")
public class SoapTransformController {


    /**
     * 方法一：用cxf框架
     *
     * @param url
     * @param method
     * @param args
     * @return
     */
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Map<String, Object> hello(String url, String method, String[] args) {
        if (args.length < 1) {
            args = new String[]{""};
        }

        // 创建动态客户端
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(url);
        // 需要密码的情况需要加上用户名和密码
        // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME,PASS_WORD));
        // 命名空间，方法名
        QName name = new QName("http://WebXml.com.cn/", method);
        HashMap<String, Object> map = new HashMap<>();
        try {
            // invoke("方法名",参数1,参数2,参数3....);
            Object[] objects = client.invoke(name, args);
            map.put("result", objects);
            return map;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            map.put("result", "接口调用异常");
            return map;
        }
    }

    /**
     * 方法二：自己用jax-ws的方式实现
     *
     * @param url             请求地址
     * @param targetNamespace 名称空间
     * @param pName           端口名
     * @param method          方法名
     * @param argsName        参数名
     * @param args            参数
     * @return
     * @throws Exception
     */
    @RequestMapping("/getSoap")
    @ResponseBody
    public Map getSoap(String url, String targetNamespace, String pName, String method, String[] argsName, String[] args) throws Exception {
        QName serviceName = new QName(targetNamespace, method);

        //WSDL中定义的端口的QName
        QName portName = new QName(targetNamespace, pName);

        //创建动态Service实例
        Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, url);

        //创建一个dispatch实例
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);

        // Use Dispatch as BindingProvider
        BindingProvider bp = (BindingProvider) dispatch;

        // 配置RequestContext以发送SOAPAction HTTP标头
        Map<String, Object> rc = dispatch.getRequestContext();
        rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, targetNamespace + method);

        // 获取预配置的SAAJ MessageFactory
        MessageFactory factory = ((SOAPBinding) bp.getBinding()).getMessageFactory();

        // 创建SOAPMessage请求
        SOAPMessage request = null;
        request = factory.createMessage();
        // 请求体
        SOAPBody body = request.getSOAPBody();

        // Compose the soap:Body payload
        QName payloadName = new QName(targetNamespace, method);

        SOAPBodyElement payload = body.addBodyElement(payloadName);
        if (args.length > 0) {
            for (int i = 0; i < argsName.length; i++) {
                payload.addChildElement(argsName[i]).setValue(args[i]);
            }
        }
//        payload.addChildElement("startCity").setValue("北京");
//        payload.addChildElement("lastCity").setValue("上海");
//        payload.addChildElement("theDate").setValue("2019-06-07");
//        payload.addChildElement("userID").setValue("");

//        SOAPElement message = payload.addChildElement(INPUT_NMAE);
//        message.addTextNode("88888");

        SOAPMessage reply = null;

        try {
            //调用端点操作并读取响应
            //request.writeTo(System.out);
            reply = dispatch.invoke(request);
            //reply.writeTo(System.out);
        } catch (WebServiceException wse) {
            wse.printStackTrace();
        }

        // 处理响应结果
        Document doc = reply.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", XML.toJSONObject(format(doc)).toString());
        //System.out.println(XML.toJSONObject(format(doc)).toString());
        return map;

    }

    // Document对象转字符串
    public static String format(Document doc) throws Exception {
        // XML转字符串
        String xmlStr = "";
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty("encoding", "UTF-8");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            t.transform(new DOMSource(doc), new StreamResult(bos));
            xmlStr = bos.toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return xmlStr;
    }


}
