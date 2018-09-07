
package com.semaifour.facesix.aws.pubsub;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.aws.pubsub.SampleUtil.KeyStorePasswordPair;
import com.semaifour.facesix.boot.Application;


/**
 * This is  uses {@link AWSIotMqttClient} to subscribe to a topic and
 * publish messages to it. Both blocking and non-blocking publishing are
 * demonstrated in this example.
 */

@Controller
public class PublishSubscribeSample {

	// Topic "$aws/things/<<Thing>>/shadow/update/accepted"
	// Endpoint "a142xisow4v1na.iot.us-east-1.amazonaws.com"
	// Certificate e1a9b9e173-certificate.pem.crt
	// Key e1a9b9e173-private.pem.key
	static Logger LOG = LoggerFactory.getLogger(PublishSubscribeSample.class.getName());		
    private static final AWSIotQos TestTopicQos = AWSIotQos.QOS0;
    private static AWSIotMqttClient awsIotClient;

    private String TestTopic 		= null;    
	private static String clientId 	= "54321";//cid    
   
    public static void setClient(AWSIotMqttClient client) {
        awsIotClient = client;
    }
    
    CustomerService customerService;

    public  class NonBlockingPublisher implements Runnable {
        private final AWSIotMqttClient awsIotClient;

        public NonBlockingPublisher(AWSIotMqttClient awsIotClient) {
            this.awsIotClient = awsIotClient;
        }

        @Override
        public void run() {
            long counter = 1;

            while (true) {
                String payload = "hello from non-blocking publisher - " + (counter++);
                AWSIotMessage message = new NonBlockingPublishListener(TestTopic, TestTopicQos, payload);
                try {
                    awsIotClient.publish(message);
                } catch (AWSIotException e) {
                	e.printStackTrace();
                	LOG.info("publish failed for " + payload);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                	e.printStackTrace();
                	LOG.info(" NonBlockingPublisher was interrupted");
                    return;
                }
            }
        }
    }

    private  void initClient(CommandArguments arguments, String cid) {
    	 
    	 LOG.info(" Entry initClient " +arguments);
    	 LOG.info(" cid " +cid);
    	 
    	 try {
    		 Customer customer = getCustomerService().findById(cid);
    			
    			if (customer != null) {
    		
    				LOG.info(" customer name  " +customer.getCustomerName());
    				 
    				TestTopic		       = customer.getAlexatopic();
    				String clientEndpoint  = customer.getAlexaendpoint();
    				String certificateFile = customer.getAlexacerfilepath();
    				String privateKeyFile  = customer.getAlexakeyfilepath();
    				clientId			   = cid;
    				
    				LOG.info(" AWS Iot Client TestTopic = " 		+ TestTopic);
    				LOG.info(" AWS Iot Client clientEndpoint = " 	+ clientEndpoint);
    				LOG.info(" AWS Iot Client certificateFile = " 	+ certificateFile);
    				LOG.info(" AWS Iot Client privateKeyFile = " 	+ privateKeyFile);
    				LOG.info(" AWS Iot Client awsIotClient  = " 	+ awsIotClient);
    				
    				if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
    		    		KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, null);
    		            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
    		        }
    		   		
    		    	LOG.info(" AWS Iot Client status " + awsIotClient);
    		        
    		   		if (awsIotClient == null) {
    		   			LOG.info("AWS Iot Client NULL  " +awsIotClient);
				}
			}
		} catch (Exception e) {
			LOG.error(" while initClient error " +e);
		}

    } 
    public void cloud_AWS_PUB_SUB (String id,String cid) throws InterruptedException, AWSIotException, AWSIotTimeoutException{
         
    	try {
    	   	 LOG.info(" *** Entry AWS_PUB_SUB *** ");

        	 initClient(null,cid);
             awsIotClient.connect();

             AWSIotTopic topic = new TestTopicListener(TestTopic, TestTopicQos,id);
             awsIotClient.subscribe(topic, true);

    	}catch(Exception e) {
    		LOG.info("while cloud_AWS_PUB_SUB init error " +e);
    	}
 
    }

    
    private CustomerService getCustomerService() {
		try {
			if (customerService == null) {
				customerService = Application.context.getBean(CustomerService.class);
			}
		} catch (Exception e) {}
		return customerService;
	}
    
}
