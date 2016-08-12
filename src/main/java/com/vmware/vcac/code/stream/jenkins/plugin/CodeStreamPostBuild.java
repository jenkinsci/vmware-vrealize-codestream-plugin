package com.vmware.vcac.code.stream.jenkins.plugin;

import com.vmware.vcac.code.stream.jenkins.plugin.model.PipelineParam;
import com.vmware.vcac.code.stream.jenkins.plugin.model.PluginParam;
import com.vmware.vcac.code.stream.jenkins.plugin.util.EnvVariableResolver;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.EnvironmentContributingAction;
import hudson.tasks.*;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static hudson.Util.fixEmptyAndTrim;

/**
 *
 * @author Kris Thieler
 */
public class CodeStreamPostBuild extends Notifier implements Serializable {

    private String serverUrl;
    private String userName;
    private String password;
    private String tenant;
    private String pipelineName;
    private boolean waitExec;
    private List<PipelineParam> pipelineParams;


    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public CodeStreamPostBuild(String serverUrl, String userName, String password, String tenant, String pipelineName, boolean waitExec, List<PipelineParam> pipelineParams) {
        this.serverUrl = fixEmptyAndTrim(serverUrl);
        this.userName = fixEmptyAndTrim(userName);
        this.password = fixEmptyAndTrim(password);
        this.tenant = fixEmptyAndTrim(tenant);
        this.pipelineName = fixEmptyAndTrim(pipelineName);
        this.waitExec = waitExec;
        this.pipelineParams = pipelineParams;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getTenant() {
        return tenant;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public List<PipelineParam> getPipelineParams() {
        return pipelineParams;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }


    public boolean isWaitExec() {
        return waitExec;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        EnvVariableResolver helper = new EnvVariableResolver(build, listener);
        PluginParam param = new PluginParam(helper.replaceBuildParamWithValue(serverUrl), helper.replaceBuildParamWithValue(userName),
                helper.replaceBuildParamWithValue(password), helper.replaceBuildParamWithValue(tenant), helper.replaceBuildParamWithValue(pipelineName), waitExec, helper.replaceBuildParamWithValue(pipelineParams));
        logger.println("Starting CodeStream pipeline execution of pipeline : " + param.getPipelineName());
        param.validate();
        CodeStreamPipelineCallable callable = new CodeStreamPipelineCallable(param);
        Map<String, String> envVariables = launcher.getChannel().call(callable);
        CodeStreamEnvAction action = new CodeStreamEnvAction();
        action.addAll(envVariables);
        build.addAction(action);
        return true;
    }


    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */


    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.

    @Override
    public BuildStepDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final CodeStreamPostBuild.DescriptorImpl DESCRIPTOR = new CodeStreamPostBuild.DescriptorImpl();


    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public String getDisplayName() {

            return "Execute CodeStream Pipeline";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }


        public FormValidation doCheckServerUrl(
                @QueryParameter final String value) {

            String url = Util.fixEmptyAndTrim(value);
            if (url == null)
                return FormValidation.error("Please enter CodeStream server URL.");

            if (url.indexOf('$') >= 0)
                // set by variable, can't validate
                return FormValidation.ok();

            try {
                new URL(value).toURI();
            } catch (MalformedURLException e) {
                return FormValidation.error("This is not a valid URI");
            } catch (URISyntaxException e) {
                return FormValidation.error("This is not a valid URI");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckUserName(
                @QueryParameter final String value) {

            String url = Util.fixEmptyAndTrim(value);
            if (url == null)
                return FormValidation.error("Please enter user name.");

            if (url.indexOf('$') >= 0)
                // set by variable, can't validate
                return FormValidation.ok();

            return FormValidation.ok();
        }

        public FormValidation doCheckPassword(
                @QueryParameter final String value) {

            String url = Util.fixEmptyAndTrim(value);
            if (url == null)
                return FormValidation.error("Please enter password.");

            if (url.indexOf('$') >= 0)
                // set by variable, can't validate
                return FormValidation.error("Environment variable cannot be used in password.");

            return FormValidation.ok();
        }

        public FormValidation doCheckTenant(
                @QueryParameter final String value) {

            String url = Util.fixEmptyAndTrim(value);
            if (url == null)
                return FormValidation.error("Please enter tenant.");

            if (url.indexOf('$') >= 0)
                // set by variable, can't validate
                return FormValidation.ok();

            return FormValidation.ok();
        }

        public FormValidation doCheckPipelineName(
                @QueryParameter final String value) {

            String url = Util.fixEmptyAndTrim(value);
            if (url == null)
                return FormValidation.error("Please enter pipeline name.");

            if (url.indexOf('$') >= 0)
                // set by variable, can't validate
                return FormValidation.ok();

            return FormValidation.ok();
        }

    }

    public static class CodeStreamEnvAction implements EnvironmentContributingAction {
        private transient Map<String, String> data = new HashMap<String, String>();

        private void add(String key, String val) {
            if (data == null) return;
            data.put(key, val);
        }

        private void addAll(Map<String, String> map) {
            data.putAll(map);
        }

        @Override
        public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
            if (data != null) env.putAll(data);
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return null;
        }

        public Map<String, String> getData() {
            return data;
        }
    }
}

