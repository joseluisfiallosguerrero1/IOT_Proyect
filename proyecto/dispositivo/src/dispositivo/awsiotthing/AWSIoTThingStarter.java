package dispositivo.awsiotthing;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

import dispositivo.utils.MySimpleLogger;

public class AWSIoTThingStarter {

    // parámetros de inicio del programa. Permite ejecutar soluciones parametrizables desde consola
    protected static final String CLIENT_ENDPOINT_KEY = "clientEndpoint";
    protected static final String CLIENT_ID_KEY = "clientId";
    protected static final String CERTIFICATE_FILE_KEY = "certificate";
    protected static final String PRIVATE_KEY_FILE_KEY = "privateKey";
    protected static final String PUBLISHER_KEY = "pub";
    protected static final String SUBSCRIBER_KEY = "sub";
    protected static final String SHADOW_KEY = "shadow";
    protected static final String SHADOW_REPORTED_VS_DESIRED_KEY = "r";   // r | d
    protected static final String TOPIC_KEY = "topic";
    protected static final String MESSAGE_KEY = "msg";
    // por ejemplo, se podrían realizar las siguientes invocaciones:
    //
    // 1.- Ejecutamos un cliente que sólo publique (no se suscriba), con la configuración por defecto de endpoint y certificados
    // $> java -jar awsiotthing.jar clientId=mi_id_unico pub=true
    // 
    // 2.- Ejecutamos un cliente que se conecte al endpoint de conexión (sin publicar ni suscribir, sólo para probar la conexión con las credenciales)
    // $> java -jar awsiotthing.jar clientId=mi_id_unico clienteEndpoint=a3c2o6jqik8b8a-ats.iot.us-east-1.amazonaws.com
    //
    // 3.- Ejecutamos un par de programas, uno que se suscriba en un topic y otro que publique en el mismo (las credenciales deben permitirlo)
    // $> java -jar awsiotthing.jar clientId=mi_suscritor sub=true topic=my/topic
    // $> java -jar awsiotthing.jar clientId=mi_publicador pub=true topic=my/topic msg=hola

    // valores por defecto de los parámetros de inicio
    protected static String clientEndpoint = "a3kqs7rhgdczgv-ats.iot.us-east-1.amazonaws.com";       // replace <prefix> and <region> with your own. Se obtiene de AWS
    protected static String clientId = "iot-object-" + UUID.randomUUID().toString();                  // replace with your own client ID. USE UNIQUE client IDs for concurrent connections.
    protected static String certsDir = "certs/";                                //Carpeta que aloja el certificado
    protected static String certificateFile = certsDir + "8ab048974966b0f77a64f8c96f7bc8f1c6db454170d8a03a5493708d10709217-certificate.pem.crt";               // X.509 based certificate file
    protected static String privateKeyFile = certsDir + "8ab048974966b0f77a64f8c96f7bc8f1c6db454170d8a03a5493708d10709217-private.pem.key";    
                    // PKCS#1 or PKCS#8 PEM encoded private key file
    
    protected static boolean publisher = false;
    protected static boolean subscriber = false;
    
    protected static boolean shadow = false;
    protected static boolean shadow_report = false;
    
    protected static String[] topics = { "smartcities/traffic/PTPaterna/road/f1", 
										 "smartcities/traffic/PTPaterna/road/f2", 
										 "smartcities/traffic/PTPaterna/road/f3" };
    protected static String payload = "{msg:hello}";

    protected static String loggerId = "my-aws-iot-thing";
    
    
    public static void main(String[] args) {
        
        // procesamos los parámetros de entrada
        processInputParameters(args);
        MySimpleLogger.info(loggerId, " Client Endpoint: " + clientEndpoint);
        MySimpleLogger.info(loggerId, "       Client Id: " + clientId);
        MySimpleLogger.info(loggerId, "Certificate File: " + certificateFile);
        MySimpleLogger.info(loggerId, "Private Key File: " + privateKeyFile);
        if ( publisher )
            MySimpleLogger.info(loggerId, "Is a PUBLISHER");
        if ( subscriber )
            MySimpleLogger.info(loggerId, "Is a SUBSCRIBER");
        

    
        AWSIotMqttClient client = initClient();
        
        // CONNECT CLIENT TO AWS IOT MQTT
        // optional parameters can be set before connect()
        AWSIotQos qos = AWSIotQos.QOS0;        
        try {
            client.connect();
            MySimpleLogger.info(loggerId, "Client Connected to AWS IoT MQTT");
            
        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        

        
        // SUBSCRIBE to multiple TOPICS
        if ( subscriber ) {
            for (String topic : topics) {
                subscribe(client, topic, qos);
            }
        }

              
        if ( publisher ) {
            /*JSONObject prop = new JSONObject();
            try {
                prop.put("message", "Hola mundo, desde el backend de AWS Iot");*/
			
			JSONObject message1 = new JSONObject();
			JSONObject message2 = new JSONObject();
			JSONObject message3 = new JSONObject();
			try {
				message1.put("message", "Hola mundo, desde el backend de AWS f1");
				message2.put("message", "Hola mundo, desde el backend de AWS f2");
				message3.put("message", "Hola mundo, desde el backend de AWS f3");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			try {
				publish(client, "smartcities/traffic/PTPaterna/road/f1", message1.toString(), qos);
				publish(client, "smartcities/traffic/PTPaterna/road/f2", message2.toString(), qos);
				publish(client, "smartcities/traffic/PTPaterna/road/f3", message3.toString(), qos);
			
            } catch (Exception e) {
                // TODO: handle exception
            }

            /*if ( !shadow )
                payload = prop.toString();
            else {
                if ( shadow_report )
                    payload = buildReportedStatusMessage(prop).toString();
                else
                    payload = buildDesiredStatusMessage(prop).toString();
            }

            for (String topic : topics) {
                publish(client, topic, payload, qos);
            }*/
        }
        
        
        
    }
    
    public static AWSIotMqttClient initClient() {
        
        // SampleUtil.java and its dependency PrivateKeyReader.java can be copied from the sample source code.
        // Alternatively, you could load key store directly from a file - see the example included in this README.
        KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        
        return client;
    }
    
    public static void subscribe(AWSIotMqttClient client, String topic, AWSIotQos qos) {
        
        
        AWSIoT_TopicHandler theTopic = new AWSIoT_TopicHandler(topic, qos);
        try {
            client.subscribe(theTopic);
            MySimpleLogger.info(loggerId, "... SUBSCRIBED to TOPIC: " + topic);
        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
    public static void publish(AWSIotMqttClient client, String topic, String payload, AWSIotQos qos) {


        // optional parameters can be set before connect()
        try {
            AWSIotMessage message = new AWSIotMessage(topic, qos, payload);
            client.publish(message);
            MySimpleLogger.info(loggerId, "... PUBLISHED message " + payload + " to TOPIC: " + topic);
        } catch (AWSIotException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
            
        
        
    }
    
    
    
    protected static JSONObject buildReportedStatusMessage(JSONObject props) {
        JSONObject r = new JSONObject();
        
        JSONObject s = new JSONObject();
        try {
            s.put("reported", props);
            r.put("state", s);
        } catch (JSONException e) {
        }
        
        
        return r;
    }
    
    protected static JSONObject buildDesiredStatusMessage(JSONObject props) {
        JSONObject r = new JSONObject();
        
        JSONObject s = new JSONObject();
        try {
            s.put("desired", props);
            r.put("state", s);
        } catch (JSONException e) {
        }
        
        
        return r;
    }
    
    
    
    protected static void processInputParameters(String[] args) {
        
        if ( args.length > 0 ) {
            
            String input;
            String[] kv;
            for(int i=0; i<args.length; i++) {
                input=args[i];
                kv = args[i].split("=");
                if ( kv[0].equalsIgnoreCase(CLIENT_ENDPOINT_KEY) ) {
                    clientEndpoint = kv[1];
                } else if ( kv[0].equalsIgnoreCase(CLIENT_ID_KEY) ) {
                    clientId = kv[1];
                } else if ( kv[0].equalsIgnoreCase(CERTIFICATE_FILE_KEY) ) {
                    certificateFile = kv[1];
                } else if ( kv[0].equalsIgnoreCase(PRIVATE_KEY_FILE_KEY) ) {
                    privateKeyFile = kv[1];
                } else if ( kv[0].equalsIgnoreCase(PUBLISHER_KEY) ) {
                    publisher = Boolean.parseBoolean(kv[1]);            
                } else if ( kv[0].equalsIgnoreCase(SUBSCRIBER_KEY) ) {
                    subscriber = Boolean.parseBoolean(kv[1]);                
                } else if ( kv[0].equalsIgnoreCase(TOPIC_KEY) ) {
                    topics = kv[1].split(",");                    
                } else if ( kv[0].equalsIgnoreCase(MESSAGE_KEY) ) {
                    payload = kv[1];                    
                } else if ( kv[0].equalsIgnoreCase(SHADOW_KEY) ) {
                    shadow = Boolean.parseBoolean(kv[1]);
                } else if ( kv[0].equalsIgnoreCase(SHADOW_REPORTED_VS_DESIRED_KEY) ) {
                    shadow_report = ( kv[1].equalsIgnoreCase("r"));
                }
                
                
            }
            
        }

        
    }


}
