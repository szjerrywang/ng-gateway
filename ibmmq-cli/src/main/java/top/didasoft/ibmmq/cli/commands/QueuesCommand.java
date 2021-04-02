package top.didasoft.ibmmq.cli.commands;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.*;

import javax.inject.Inject;
import java.io.IOException;

@Command(name = "queues", description = "A command that query all queues in specific queuemanager")
public class QueuesCommand extends BaseCommand {

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
    private HelpOption<QueuesCommand> help;


    @Override
    public int run() {
        if (help.showHelpIfRequested())
            return 0;

        PCFAgent pcfAgent = null;

        try {
            MQQueueManager queueManager = createMQQueueManager();
            pcfAgent = new PCFAgent(queueManager);

            MQMessage[] responses;
            PCFParameter[] parameters = {new MQCFST(CMQC.MQCA_Q_NAME, "*"),
                    new MQCFIN(CMQC.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL)};

            MQCFH cfh;
            MQCFSL cfsl;

            responses = pcfAgent.send(CMQCFC.MQCMD_INQUIRE_Q_NAMES, parameters);

            cfh = new MQCFH(responses[0]);

            if (cfh.getReason() == 0) {
                cfsl = new MQCFSL(responses[0]);

                for (int i = 0; i < cfsl.getStrings().length; i++) {
                    System.out.println("Queue: " + cfsl.getStrings()[i]);
                }
            } else {
                throw new MQDataException(cfh.getCompCode(), cfh.getReason(), pcfAgent);
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
            if (pcfAgent != null) {
                try {
                    pcfAgent.disconnect();
                } catch (MQDataException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }
}
