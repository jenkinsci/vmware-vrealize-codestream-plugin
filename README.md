# Jenkins CodeStream Plugin
---------------------
This plugin integrates [VMware vRealize CodeStream][] to [Jenkins][]. With this plugin you can run any vRealize CodeStream activated pipeline.

[VMware vRealize CodeStream]: https://www.vmware.com/products/vrealize-code-stream
[Jenkins]: https://jenkins-ci.org/

Configuration
-------------

1) Build step : On Job configuration page click on Add build step select “Execute CodeStream Pipeline” option

![Build step](/doc/add-build-step.png)

2) Configure :  Configure CodeStream pipeline like show in image. Below is the description of each field

  * Server URl -   vRealize CodeStream host URL
  * Tenant - User group tenant against which you want to execute the pipeline
  * User Name - Username for given tenant. User should have Release Manager or Release Engineer Role.
  * Password - Password for given user
  * Release Pipeline Name - Pipeline which you want to execute. Pipeline should be activate and its latest version will be executed.
  * Execute and Wait - If this checkbox is checked the job will wait for pipeline execution to complete or fail.
  * Add Parameter - If you want to override default value of any pipeline property then you can use this option. Click on Add Parameter and provide property name in Parameter Name. New  value in Parameter Value. For all the other properties default value will be used.

![Configure](/doc/configuration.png)


Jenkins version supported
------------------------
1.565 and above


Development
===========

Start the local Jenkins instance:

    mvn hpi:run


Installing
----------
Run

	mvn hpi:hpi

to create the plugin .hpi file.


To install:

1. copy the resulting ./target/vmware-vrealize-codestream-plugin.hpi file to the $JENKINS_HOME/plugins directory. Don't forget to restart Jenkins afterwards.

2. or use the plugin management console (http://example.com:8080/pluginManager/advanced) to upload the hpi file. You have to restart Jenkins in order to find the pluing in the installed plugins list.

Read More
----------
https://wiki.jenkins-ci.org/display/JENKINS/Vmware+vRealize+CodeStream+Plugin

License
----------
MIT Licensed


Maintainer
----------
Rishi Saraf <rishisaraf11@gmail.com>

