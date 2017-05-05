package io.hops.hopsworks.api.airpal;

import io.hops.hopsworks.api.kibana.ProxyServlet;
import io.hops.hopsworks.common.dao.jobhistory.YarnApplicationstateFacade;
import io.hops.hopsworks.common.dao.user.security.ua.UserManager;
import io.hops.hopsworks.common.hdfs.HdfsUsersController;
import io.hops.hopsworks.common.project.ProjectController;
import java.io.IOException;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AirpalProxyServlet extends ProxyServlet {
  
  private final static Logger logger = Logger.getLogger(AirpalProxyServlet.class.
    getName());
  
  @EJB
  private YarnApplicationstateFacade yarnApplicationstateFacade;
  @EJB
  private UserManager userManager;
  @EJB
  private HdfsUsersController hdfsUsersBean;
  @EJB
  private ProjectController projectController;
  
  @Override
  protected void service(HttpServletRequest request,
    HttpServletResponse response)
    throws ServletException, IOException {
    
    String email = request.getUserPrincipal().getName();
    
    String path = request.getParameter("projectID");
    logger.fine("PATH ===========================================" + path);
    logger.fine("email ===========================================" + email);
    
    super.service(request, response);
    
  }
  
}
