package acme;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;


public class Main {
    public static void main(String[] args) {

        String message = "Hi from java";
        String topicArn = "arn:aws:sns:us-west-2:132986401742:greg-delme";
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();
        pubTopic(snsClient, message, topicArn);
        snsClient.close();
        
        
        String queueName = "https://sqs.us-west-2.amazonaws.com/132986401742/GregDelme";
        String message2 = "Hello from Java to SQS";
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();
        sendMessage(sqsClient, queueName, message2);
        sqsClient.close();
    }

    public static void pubTopic(SnsClient snsClient, String message, String topicArn) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out
                    .println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void sendMessage(SqsClient sqsClient, String queueName, String message) {
        try {
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueName)
                    .messageBody(message)
                    .delaySeconds(5)
                    .build();

            SendMessageResponse respo = sqsClient.sendMessage(sendMsgRequest);
            System.out.println("SQS message sent. Status code: " + respo.sdkHttpResponse().statusCode());

        } catch (SqsException e) {
            System.out.println("BAD!");
            System.err.println(e.awsErrorDetails().errorMessage());
            System.err.println(e.toString());
            System.exit(1);
        }
    }
}
