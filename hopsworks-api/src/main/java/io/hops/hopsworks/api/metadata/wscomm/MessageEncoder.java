package io.hops.hopsworks.api.metadata.wscomm;

import io.hops.hopsworks.api.metadata.wscomm.message.Message;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

  private static final Logger logger = Logger.getLogger(MessageDecoder.class.
          getName());

  @Override
  public String encode(Message msg) throws EncodeException {

    String value = msg.encode();

    //logger.log(Level.INFO, "RETURNED VALUE IS {0}", value);
    return value;
  }

  @Override
  public void init(final EndpointConfig config) {
  }

  @Override
  public void destroy() {
  }

}
