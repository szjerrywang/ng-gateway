java -Djavax.net.debug=ssl -Djavax.net.ssl.keyStore=/home/centos/tiger/ng-gateway/ibmmq/keystore.jks -Djavax.net.ssl.trustStore=/home/centos/tiger/ng-gateway/ibmmq/keystore.jks -Djavax.net.ssl.trustStorePassword=123456 -Djavax.net.ssl.keyStorePassword=123456 -jar target/ibmmq-0.0.1.jar --spring.config.location=file:/home/centos/.mqs/application.yml

