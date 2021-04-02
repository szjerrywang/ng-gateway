package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.*;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Enumeration;

@Command(name = "queueinfo", description = "A command that query attributes of a queue in specific queuemanager")
public class QueueInfoCommand extends BaseCommand {

    /**
     * The special {@link HelpOption} provides a {@code -h} and {@code --help}
     * option that can be used to request that help be shown.
     * <p>
     * Developers need to check the {@link HelpOption#showHelpIfRequested()}
     * method which will display help if requested and return {@code true} if
     * the user requested the help
     * </p>
     */
    @Inject
    private HelpOption<QueueInfoCommand> help;

    @Option(name = { "--queueName" }, title = "QueueName", arity = 1, description = "Queue name to query")
    @Required
    protected String queueName;


    @Override
    public int run() {
        if (help.showHelpIfRequested())
            return 0;

        PCFMessageAgent pcfMessageAgent = null;

        try {
            MQQueueManager queueManager = createMQQueueManager();

            pcfMessageAgent = new PCFMessageAgent(queueManager);
            pcfMessageAgent.setCheckResponses(true);

            PCFMessage[] responses;
            PCFParameter[] parameters = {new MQCFST(CMQC.MQCA_Q_NAME, queueName)};


            PCFMessage   request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
            request.addParameter(new MQCFST(CMQC.MQCA_Q_NAME, queueName));

            responses = pcfMessageAgent.send(request);

            PCFMessage msg = responses[0];

            if (msg.getReason() == 0) {
                Enumeration msgParameters = msg.getParameters();
                while (msgParameters.hasMoreElements()) {
                    Object element = msgParameters.nextElement();
                    if (element instanceof MQCFST) {
                        MQCFST mqcfst = (MQCFST) element;
                        System.out.println("Name: " + mqcfst.getParameterName() + ", Value: " + mqcfst.getStringValue());
                    }
                    else if (element instanceof MQCFBS) {
                        MQCFBS mqcfbs = (MQCFBS) element;
                        System.out.println("Name: " + mqcfbs.getParameterName() + ", Value: " + mqcfbs.getStringValue());

                    }
                    else if (element instanceof MQCFIN) {
                        MQCFIN mqcfin = (MQCFIN) element;
                        System.out.println("Name: " + mqcfin.getParameterName() + ", Value: " + mqcfin.getIntValue());

                    }
                    else if (element instanceof MQCFIN64) {
                        MQCFIN64 mqcfin64 = (MQCFIN64) element;
                        System.out.println("Name: " + mqcfin64.getParameterName() + ", Value: " + mqcfin64.getLongValue());

                    }
                    else if (element instanceof MQCFIL) {
                        MQCFIL mqcfil = (MQCFIL) element;
                        System.out.println("Name: " + mqcfil.getParameterName() + ", Value: " + mqcfil.getValues());

                    }
                    else if (element instanceof  MQCFIL64) {
                        MQCFIL64 mqcfil = (MQCFIL64) element;
                        System.out.println("Name: " + mqcfil.getParameterName() + ", Value: " + mqcfil.getValues());

                    }
                    else if (element instanceof MQCFSL) {
                        MQCFSL mqcfsl = (MQCFSL) element;
                        final String value = StringUtils.join(mqcfsl.getStrings(), ',');
                        System.out.println("Name: " + mqcfsl.getParameterName() + ", Value: " + value);

                    }
                    else {
                        System.out.println(element.toString());
                    }
                }

            } else {
                throw new MQDataException(msg.getCompCode(), msg.getReason(), pcfMessageAgent);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        } catch (MQDataException e) {
            e.printStackTrace();
            return 2;
        } catch (MQException e) {
            e.printStackTrace();
            return 3;
        } finally {
            if (pcfMessageAgent != null) {
                try {
                    pcfMessageAgent.disconnect();
                } catch (MQDataException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }
}
