/*
 * Sample program for use with Product
 *  ProgIds: 5724-J06 5724-J05 5724-J04 5697-J09 5655-M74 5655-M75 5648-C63
 *  (C) Copyright IBM Corporation 2004.
 * All Rights Reserved * Licensed Materials - Property of IBM
 *
 * This sample program is provided AS IS and may be used, executed,
 * copied and modified without royalty payment by customer
 *
 * (a) for its own instruction and study,
 * (b) in order to develop applications designed to run with an IBM
 *     product, either for customer's own internal use or for
 *     redistribution by customer, as part of such an application, in
 *     customer's own products.
 */
package iapi.common;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/*****************************************************************************
 * <P>The ResourcesHandler is used by several sample programs.
 *
 * <P><TABLE BORDER="1" BORDERCOLOR="#000000" CELLSPACING="0"
 * CELLPADDING="5" WIDTH="100%">
 * <TR>
 *   <TD COLSPAN="2" ALIGN="LEFT" VALIGN="TOP" BGCOLOR="#C0FFC0">
 *     <B><I>cmp.ResourcesHandler</I></B><P>
 *   </TD>
 * </TR>
 * <TR>
 *   <TD WIDTH="18%" ALIGN="LEFT" VALIGN="TOP">Responsibilities</TD>
 *   <TD WIDTH="*" ALIGN="LEFT" VALIGN="TOP">
 *     Provides services to provide localised strings and user settings.
 *   </TD>
 * </TR>
 * <TR>
 *   <TD WIDTH="18%" ALIGN="LEFT" VALIGN="TOP">Internal Collaborators</TD>
 *   <TD WIDTH="*" ALIGN="LEFT" VALIGN="TOP">
 *     None
 *   </TD>
 * </TR>
 * </TABLE>
 *
 * <pre>
 * Change Activity:
 * -------- ----------- -------------   ------------------------------------
 * Reason:  Date:       Originator:     Comments:
 * -------- ----------- -------------   ------------------------------------
 * 25103.7  2004-03-18  HDMPL           v6 Release
 * 35108.6  2006-01-24  HDMPL           Support for 64-bit execution groups
 * 44739.7  2007-05-03  HDMPL           Updates for Java 5
 * 51619.1  2008-07-15  HDMPL           Updates for MB v7
 * 80006.1  2011-02-02  HDCAB           Updates for MB v8
 *                                      - Applications and Libraries
 * 80006.2  2011-04-18  HDCAB           - Activity Log support
 * </pre>
 *
 * @version %W% %I%
 *****************************************************************************/
public class ResourcesHandler {

    /**
     * This is the ResourcesHandler which supplies
     * translatable strings
     */
    private static ResourcesHandler nlsHandler = null;

    /**
     * This is the ResourcesHandler which supplies
     * user configurable settings
     */
    private static ResourcesHandler settingsHandler = null;

    /**
     * The bundle of resources managed by this handler instance
     */
    private ResourceBundle resources;

    /**
     * Cache of the resources contained in the bundle
     */
    private Properties resourcesCache = null;

    /**
     * This constant describes the name of the bundle from which
     * translatable strings should be obtained. The naming rules for
     * Java resource bundles apply.
     */
    private final static String NLS_RESOURCE_FILENAME = "iapi.common.IAPISamples_Resources";

    /**
     * This constant describes the name of the bundle from which
     * user settings should be obtained. The naming rules for
     * Java resource bundles apply.
     */
    private final static String SETTINGS_RESOURCE_FILENAME = "IAPISamples_UserSettings";


    // A set of strings that describe the keys used to look up
    // string constants in the NLS bundle or user settings bundle
    public static final String WINDOW_TITLE = "exerciser.windowtitle";
    public static final String WELCOME = "exerciser.welcome";
    public static final String CONSOLE_CHANGED = "exerciser.console_changed";
    public static final String CLEAR_CONSOLE = "exerciser.clearconsole";
    public static final String COPY = "exerciser.copy";
    public static final String SELECT_TRACE_OUTPUT = "exerciser.select_trace_output";
    public static final String SELECT_SCRIPT_OUTPUT = "exerciser.select_script_output";
    public static final String SELECT_SCRIPT_INPUT = "exerciser.select_script_input";
    public static final String SCRIPT_WAIT_TIME = "exerciser.script_wait_time";
    public static final String BROKER_LONG_DESC = "exerciser.broker_long_desc";
    public static final String BROKER_SHORT_DESC = "exerciser.broker_short_desc";
    public static final String EG_LONG_DESC = "exerciser.eg_long_desc";
    public static final String EG_SHORT_DESC = "exerciser.eg_short_desc";
    public static final String SELECT_BAR = "exerciser.select_bar";
    public static final String SELECT_POLICY = "exerciser.select_policy";
    public static final String FILE = "exerciser.file";
    public static final String ACTION_ADDED_TO_BATCH = "exerciser.action_added_to_batch";
    public static final String ACTION_SENT_TO_BROKER = "exerciser.action_sent_to_broker";
    public static final String ACTION_COMPLETED = "exerciser.action_completed";
    public static final String ACTION_FAILED = "exerciser.action_failed";
    public static final String ACTION_TIMEDOUT = "exerciser.action_timedout";
    public static final String PROPERTY_NAME = "exerciser.property_name";
    public static final String PROPERTY_VALUE = "exerciser.property_value";
    public static final String SELECTED = "exerciser.selected";
    public static final String PROGRAM_STARTED = "exerciser.program_started";
    public static final String ADMINISTEREDOBJECT_REFRESH = "exerciser.refresh";
    public static final String ADMINISTEREDOBJECT_GETCHILDREN = "exerciser.get_children";
    public static final String ADMINISTEREDOBJECT_RAWPROPERTIES = "exerciser.raw_properties";
    public static final String FILE_CONNECT_LOCAL_BROKER = "exerciser.connect_local_broker";
    public static final String FILE_CONNECT_REMOTE_BROKER = "exerciser.connect_remote_broker";
    public static final String FILE_DISCONNECT = "exerciser.disconnect";
    public static final String FILE_CMP_TRACE = "exerciser.cmp_trace";
    public static final String FILE_RENDERCONSOLEINHTML = "exerciser.render_console_in_html";
    public static final String BROKER_CREATEEG = "exerciser.broker_create_eg";
    public static final String BROKER_CREATEPOLICY = "exerciser.broker_create_policy";
    public static final String BROKER_CREATEPOLICYPROXY = "exerciser.broker_create_policy_proxy";
    public static final String BROKER_UPDATEPOLICY = "exerciser.broker_update_policy";
    public static final String BROKER_UPDATEPOLICYPROXY = "exerciser.broker_update_policy_proxy";
    public static final String BROKER_DELETEPOLICY = "exerciser.broker_delete_policy";
    public static final String BROKER_DELETEPOLICYPROXY = "exerciser.broker_delete_policy_proxy";
    public static final String BROKER_COPYPOLICY = "exerciser.broker_copy_policy";
    public static final String EG_DELETE = "exerciser.eg_delete";
    public static final String BROKER_DEPLOY = "exerciser.broker_deploy";
    public static final String BROKER_DELETEALLEGSANDDEPLOY = "exerciser.broker_delete_and_deploy";
    public static final String SERVICE_TRACE = "exerciser.service_trace";
    public static final String EG_STARTFLOWS = "exerciser.eg_start_flows";
    public static final String EG_STOPFLOWS = "exerciser.eg_stop_flows";
    public static final String EG_STARTAPPLICATIONS = "exerciser.eg_start_applications";
    public static final String EG_STOPAPPLICATIONS = "exerciser.eg_stop_applications";
    public static final String APPLICATION_START = "exerciser.application_start";
    public static final String APPLICATION_STOP = "exerciser.application_stop";
    public static final String EG_START = "exerciser.eg_start";
    public static final String EG_STOP = "exerciser.eg_stop";
    public static final String EG_USERTRACE = "exerciser.eg_user_trace";
    public static final String EG_STARTUSERTRACE = "exerciser.eg_start_user_trace";
    public static final String EG_DEBUGUSERTRACE = "exerciser.eg_debug_user_trace";
    public static final String EG_STOPUSERTRACE = "exerciser.eg_stop_user_trace";
    public static final String EG_RESOURCE_STATISTICS = "exerciser.eg_resource_statistics";
    public static final String EG_RESOURCE_STATISTICS_ENABLE = "exerciser.eg_resource_statistics_enable";
    public static final String EG_RESOURCE_STATISTICS_DISABLE = "exerciser.eg_resource_statistics_disable";
    public static final String EG_RESOURCE_STATISTICS_LABEL = "exerciser.eg_resource_statistics_label";
    public static final String EG_RESOURCE_STATISTICS_ALL = "exerciser.eg_resource_statistics_all";
    public static final String BROKER_RUNTIME_PROPERTIES = "exerciser.broker_runtime_properties";
    public static final String BROKER_SET_RUNTIME_PROPERTY = "exerciser.broker_set_runtime_property";
    public static final String BROKER_SET_HTTP_LISTENER_RUNTIME_PROPERTY = "exerciser.broker_set_http_listener_runtime_property";
    public static final String BROKER_SET_HTTP_LISTENER_RUNTIME_PROPERTY_PROPERTY_NAME = "exerciser.broker_set_http_listener_runtime_property_property_name";
    public static final String BROKER_SET_HTTP_LISTENER_RUNTIME_PROPERTY_PROPERTY_VALUE = "exerciser.broker_set_http_listener_runtime_property_property_value";
    public static final String BROKER_SET_REGISTRY_RUNTIME_PROPERTY = "exerciser.broker_set_registry_runtime_property";
    public static final String BROKER_SET_REGISTRY_RUNTIME_PROPERTY_PROPERTY_NAME = "exerciser.broker_set_registry_runtime_property_property_name";
    public static final String BROKER_SET_REGISTRY_RUNTIME_PROPERTY_PROPERTY_VALUE = "exerciser.broker_set_registry_runtime_property_property_value";
    public static final String BROKER_SET_SECURITY_CACHE_RUNTIME_PROPERTY = "exerciser.broker_set_security_cache_runtime_property";
    public static final String BROKER_SET_SECURITY_CACHE_RUNTIME_PROPERTY_PROPERTY_NAME = "exerciser.broker_set_security_cache_runtime_property_property_name";
    public static final String BROKER_SET_SECURITY_CACHE_RUNTIME_PROPERTY_PROPERTY_VALUE = "exerciser.broker_set_security_cache_runtime_property_property_value";
    public static final String BROKER_PROPERTIES = "exerciser.broker_properties";
    public static final String BROKER_POLICY_SETS = "exerciser.broker_policy_sets";
    public static final String BROKER_DOES_NOT_EXIST = "exerciser.broker_does_not_exist";
    public static final String BROKER_POLICY_SET_NAME = "exerciser.broker_policy_set_name";
    public static final String BROKER_POLICY_SET_BINDINGS_NAME = "exerciser.broker_policy_set_bindings_name";
    public static final String BROKER_GET_POLICY_SET = "exerciser.broker_get_policy_set";
    public static final String BROKER_GET_POLICY_SET_BINDINGS = "exerciser.broker_get_policy_set_bindings";
    public static final String BROKER_IMPORT_POLICY_SET = "exerciser.broker_import_policy_set";
    public static final String BROKER_IMPORT_POLICY_SET_BINDINGS = "exerciser.broker_import_policy_set_bindings";
    public static final String BROKER_DELETE_POLICY_SET = "exerciser.broker_delete_policy_set";
    public static final String BROKER_DELETE_POLICY_SET_BINDINGS = "exerciser.broker_delete_policy_set_bindings";
    public static final String EG_RESTART_NEEDED = "exerciser.eg_restart_needed";
    public static final String EG_SET_DEBUG_PORT = "exerciser.eg_set_debug_port";
    public static final String EG_SET_DEBUG_PORT_PROPERTY_VALUE = "exerciser.eg_set_debug_port_property_value";
    public static final String EG_SET_RUNTIME_PROPERTY = "exerciser.eg_set_runtime_property";
    public static final String EG_SET_RUNTIME_PROPERTY_PROPERTY_NAME = "exerciser.eg_set_runtime_property_property_name";
    public static final String EG_SET_RUNTIME_PROPERTY_PROPERTY_VALUE = "exerciser.eg_set_runtime_property_property_value";
    public static final String EG_DEPLOY = "exerciser.eg_deploy";
    public static final String EG_DELETEDEPLOYED = "exerciser.eg_delete_deployed";
    public static final String EG_PROPERTIES = "exerciser.eg_properties";
    public static final String EG_DOES_NOT_EXIST = "exerciser.eg_does_not_exist";
    public static final String GET_ACTIVITY_LOG = "exerciser.get_activity_log";
    public static final String MFD_DELETE = "exerciser.mfd_delete";
    public static final String BAR_VIEW = "exerciser.bar_view";
    public static final String BAR_REDEPLOY = "exerciser.bar_redeploy";
    public static final String BAR_DEPLOY_DESCRIPTOR = "exerciser.bar_deploy_descriptor";
    public static final String MF_START = "exerciser.mf_start";
    public static final String MF_STOP = "exerciser.mf_stop";
    public static final String MF_DELETE = "exerciser.mf_delete";
    public static final String MF_STARTUSERTRACE = "exerciser.mf_start_user_trace";
    public static final String MF_DEBUGUSERTRACE = "exerciser.mf_debug_user_trace";
    public static final String MF_STOPUSERTRACE = "exerciser.mf_stop_user_trace";
    public static final String MF_STATISTICS = "exerciser.mf_statistics";
    public static final String MF_STATISTICS_SNAPSHOT = "exerciser.mf_statistics_snapshot";
    public static final String MF_STATISTICS_SNAPSHOT_ENABLED = "exerciser.mf_statistics_snapshot_enabled";
    public static final String MF_STATISTICS_SNAPSHOT_NODE_DETAIL = "exerciser.mf_statistics_snapshot_node_detail";
    public static final String MF_STATISTICS_SNAPSHOT_THREAD_DETAIL = "exerciser.mf_statistics_snapshot_thread_detail";
    public static final String MF_STATISTICS_SNAPSHOT_FORMAT = "exerciser.mf_statistics_snapshot_format";
    public static final String MF_STATISTICS_SNAPSHOT_ACCOUNTING_ORIGIN = "exerciser.mf_statistics_snapshot_accounting_origin";
    public static final String MF_STATISTICS_ARCHIVE = "exerciser.mf_statistics_archive";
    public static final String MF_STATISTICS_ARCHIVE_ENABLED = "exerciser.mf_statistics_archive_enabled";
    public static final String MF_STATISTICS_ARCHIVE_NODE_DETAIL = "exerciser.mf_statistics_archive_node_detail";
    public static final String MF_STATISTICS_ARCHIVE_THREAD_DETAIL = "exerciser.mf_statistics_archive_thread_detail";
    public static final String MF_STATISTICS_ARCHIVE_FORMAT = "exerciser.mf_statistics_archive_format";
    public static final String MF_STATISTICS_ARCHIVE_ACCOUNTING_ORIGIN = "exerciser.mf_statistics_archive_accounting_origin";
    public static final String MF_STATISTICS_ARCHIVE_RESET = "exerciser.mf_statistics_archive_reset";
    public static final String MF_SET_ADDITIONAL_INSTANCES = "exerciser.mf_set_additional_instances";
    public static final String MF_SET_ADDITIONAL_INSTANCES_VALUE = "exerciser.mf_set_additional_instances_value";
    public static final String MF_SET_RUNTIME_PROPERTY = "exerciser.mf_set_runtime_property";
    public static final String MF_SET_RUNTIME_PROPERTY_PROPERTY_NAME = "exerciser.mf_set_runtime_property_property_name";
    public static final String MF_SET_RUNTIME_PROPERTY_PROPERTY_VALUE = "exerciser.mf_set_runtime_property_property_value";
    public static final String MF_SET_USER_DEFINED_PROPERTY = "exerciser.mf_set_user_defined_property";
    public static final String MF_SET_USER_DEFINED_PROPERTY_PROPERTY_NAME = "exerciser.mf_set_user_defined_property_property_name";
    public static final String MF_SET_USER_DEFINED_PROPERTY_PROPERTY_VALUE = "exerciser.mf_set_user_defined_property_property_value";
    public static final String MF_PROPERTIES = "exerciser.mf_properties";
    public static final String MF_NAME = "exerciser.mf_name";
    public static final String MF_LONG_DESC = "exerciser.mf_long_desc";
    public static final String MF_SHORT_DESC = "exerciser.mf_short_desc";
    public static final String START_SERVICE_TRACE = "exerciser.start_service_trace";
    public static final String DEBUG_SERVICE_TRACE = "exerciser.debug_service_trace";
    public static final String STOP_SERVICE_TRACE = "exerciser.stop_service_trace";
    public static final String LOG_DISPLAY = "exerciser.log_display";
    public static final String LOG_CLEAR = "exerciser.log_clear";
    public static final String ADMINQUEUE_DISPLAY = "exerciser.adminqueue_display";
    public static final String ADMINQUEUE_CANCEL = "exerciser.adminqueue_cancel";
    public static final String ADMINQUEUE_WORKID = "exerciser.adminqueue_workid";
    public static final String ADMINQUEUE_EMPTY = "exerciser.adminqueue_empty";
    public static final String ADMINQUEUE_NOTHINGTOCANCEL = "exerciser.adminqueue_nothingtocancel";
    public static final String CONFIGURABLE_SERVICES_FOLDER_NAME = "exerciser.configurable_services_folder_name";
    public static final String CONFIGURABLE_SERVICE_ADD = "exerciser.configurable_service_add";
    public static final String CONFIGURABLE_SERVICE_DELETE = "exerciser.configurable_service_delete";
    public static final String CONFIGURABLE_SERVICE_MODIFY = "exerciser.configurable_service_modify";
    public static final String CONFIGURABLE_SERVICE_TYPE = "exerciser.configurable_service_type";
    public static final String CONFIGURABLE_SERVICE_NAME = "exerciser.configurable_service_name";
    public static final String CONFIGURABLE_SERVICE_PROPERTY_NAME = "exerciser.configurable_service_property_name";
    public static final String CONFIGURABLE_SERVICE_PROPERTY_VALUE = "exerciser.configurable_service_property_value";
    public static final String NO_OPTIONS_AVAILABLE = "exerciser.no_options_available";
    public static final String YES_INPUT_STRING_IDENTIFIER = "exerciser.yes_input_string_identifier";
    public static final String CONNECTION_COMPLETED_BROKER = "exerciser.connection_completed_broker";
    public static final String NOT_CONNECTED = "exerciser.not_connected";
    public static final String NOT_RUNNING = "exerciser.not_running";
    public static final String AUTOMATION_WARNING = "exerciser.automation_warning";
    public static final String ADDED_AUTOMATION_ACTION = "exerciser.added_automation_action";
    public static final String ACTION_IGNORED = "exerciser.action_ignored";
    public static final String RECORDING_STARTED = "exerciser.recording_started";
    public static final String RECORDING_STOPPED = "exerciser.recording_stopped";
    public static final String PLAYBACK_STARTED = "exerciser.playback_started";
    public static final String PLAYBACK_INFO = "exerciser.playback_info";
    public static final String PLAYBACK_FINISHED = "exerciser.playback_finished";
    public static final String PLAYBACK_FILE_NOT_FOUND = "exerciser.playback_file_not_found";
    public static final String PLAYBACK_FILE_NOT_READABLE = "exerciser.playback_file_not_readable";
    public static final String PLAYBACK_FILE_EMPTY = "exerciser.playback_file_empty";
    public static final String COMMAND_IGNORED = "exerciser.command_ignored";
    public static final String BATCH_START = "exerciser.batch_start_info";
    public static final String BATCH_SENT = "exerciser.batch_sent_info";
    public static final String BATCH_CLEARED = "exerciser.batch_cleared_info";
    public static final String FILE_RETRYCHARS = "exerciser.file_retrychars";
    public static final String FILE_QUIT = "exerciser.file_exit";
    public static final String FILE_BATCHCLEAR = "exerciser.batch_cleared_menu";
    public static final String FILE_BATCHSEND = "exerciser.batch_send_menu";
    public static final String FILE_BATCHSTART = "exerciser.batch_start_menu";
    public static final String FILE_INSTALLINFO = "exerciser.install_info";
    public static final String FILE_ISDELTA = "exerciser.is_delta";
    public static final String VIEW = "exerciser.view";
    public static final String VIEW_LEVEL_0 = "exerciser.view_level_0";
    public static final String VIEW_LEVEL_1 = "exerciser.view_level_1";
    public static final String VIEW_LEVEL_2 = "exerciser.view_level_2";
    public static final String VIEW_GROUPBYBAR = "exerciser.view_group_by_bar";
    public static final String VIEW_DISPLAYPOLICIES = "exerciser.view_display_policies";
    public static final String AUTOMATION = "exerciser.automation";
    public static final String AUTOMATION_RECORD = "exerciser.automation_record";
    public static final String AUTOMATION_PAUSE = "exerciser.automation_pause";
    public static final String AUTOMATION_PLAY = "exerciser.automation_play";
    public static final String AUTOMATION_STOP = "exerciser.automation_stop";
    public static final String ADDTOBATCH = "exerciser.add_to_batch";
    public static final String SUBMIT = "exerciser.submit";
    public static final String CANCEL = "exerciser.cancel";
    public static final String NEW_SUBCOMPONENT = "exerciser.new_subcomponent";
    public static final String REMOVED_SUBCOMPONENT = "exerciser.removed_subcomponent";
    public static final String CHANGED_ATTRIBUTE = "exerciser.changed_attribute";
    public static final String DELETED_ATTRIBUTE = "exerciser.deleted_attribute";
    public static final String LOG_ENTRY = "exerciser.log_entry";
    public static final String REFERENCE_PROPERTY = "exerciser.reference_property";
    public static final String NO_CUSTOM_TEST_DEFINED = "exerciser.no_custom_test_defined";
    public static final String NEVER = "exerciser.never";
    public static final String CONSOLE_SAVED = "exerciser.console_saved";
    public static final String CMP_TRACE_STARTED = "exerciser.cmp_trace_started";
    public static final String CMP_TRACE_STOPPED = "exerciser.cmp_trace_stopped";
    public static final String NO_DEPLOYED_DEPENDENCIES = "exerciser.no_deployed_dependencies";
    public static final String LOG_EMPTY = "exerciser.log_empty";
    public static final String INVALID_TYPE = "exerciser.invalid_type";
    public static final String INVALID_ARGUMENTS = "exerciser.invalid_arguments";
    public static final String INVALID_PERMISSION = "exerciser.invalid_permission";
    public static final String ADVANCED = "exerciser.advanced";
    public static final String NOT_APPLICABLE = "exerciser.not_applicable";
    public static final String OBJECT_UNAVAILABLE = "exerciser.object_unavailable";
    public static final String BROKERINFO_HELP = "brokerinfo.help";
    public static final String BROKER_RUNNING = "brokerinfo.broker_running";
    public static final String EG_RUNNING = "brokerinfo.eg_running";
    public static final String APP_RUNNING = "brokerinfo.app_running";
    public static final String MF_RUNNING = "brokerinfo.mf_running";
    public static final String BROKER_STOPPED = "brokerinfo.broker_stopped";
    public static final String EG_STOPPED = "brokerinfo.eg_stopped";
    public static final String APP_STOPPED = "brokerinfo.app_stopped";
    public static final String MF_STOPPED = "brokerinfo.mf_stopped";
    public static final String EG_DELETED = "brokerinfo.eg_deleted";
    public static final String APP_DELETED = "brokerinfo.app_deleted";
    public static final String MF_DELETED = "brokerinfo.mf_deleted";
    public static final String EG_ADDED = "brokerinfo.eg_added";
    public static final String APP_ADDED = "brokerinfo.app_added";
    public static final String MF_ADDED = "brokerinfo.mf_added";
    public static final String LISTENING = "brokerinfo.listening";
    public static final String CONNECT_FAILED = "common.connect_failed";
    public static final String CONNECTING = "common.connecting";
    public static final String CONNECT_IN_PROGRESS = "common.connect_in_progress";
    public static final String CONNECTED_TO_BROKER_ON = "common.connected_to_broker_on";
    public static final String CONNECTED_TO_BROKER = "common.connected_to_broker";
    public static final String REGISTERED_LISTENER = "common.registered_listener";
    public static final String REGISTERED_LISTENER_BROKER = "common.registered_listener_broker";
    public static final String REGISTERED_LISTENER_EG = "common.registered_listener_eg";
    public static final String REGISTERED_LISTENER_APPL = "common.registered_listener_appl";
    public static final String REGISTERED_LISTENER_LIB = "common.registered_listener_lib";
    public static final String REGISTERED_LISTENER_SHLIB = "common.registered_listener_shlib";
    public static final String REGISTERED_LISTENER_MF = "common.registered_listener_mf";
    public static final String REGISTERED_LISTENER_SF = "common.registered_listener_sf";
    public static final String REGISTERED_LISTENER_RM = "common.registered_listener_rm";
    public static final String REGISTERED_LISTENER_EM = "common.registered_listener_em";
    public static final String REGISTERED_LISTENER_EVENT = "common.registered_listener_event";
    public static final String REGISTERED_LISTENER_PM = "common.registered_listener_pm";
    public static final String REGISTERED_LISTENER_POLICY = "common.registered_listener_policy";
    public static final String REGISTERED_LISTENER_LOG = "common.registered_listener_log";
    public static final String REGISTERED_LISTENER_AQ = "common.registered_listener_aq";
    public static final String NO_RESPONSE_FROM_BROKER = "common.no_response_from_broker";
    public static final String DISCONNECTED = "common.disconnected";
    public static final String COMMAND_THREAD_BUSY = "exerciser.command_thread_busy";
    public static final String PLEASE_WAIT = "exerciser.please_wait";
    public static final String PLEASE_WAIT_VERBOSE = "exerciser.please_wait_verbose";
    public static final String INCOMPATIBLE_SCRIPT_VERSION = "exerciser.incompatible_script";
    public static final String INCREMENTAL_DEPLOY = "exerciser.incremental_deploy";
    public static final String GROUP_BY_BAR = "exerciser.group_by_bar";
    public static final String DISPLAY_POLICIES = "exerciser.display_policies";
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USE_SSL = "useSSL";
    public static final String LOGGER_CLASSNAME = "exerciser.logger_classname";
    public static final String LOGGER_PARAMETER = "exerciser.logger_parameter";
    public static final String SAVE_CONSOLE = "exerciser.saveconsole";
    public static final String BROKER_NAME = "exerciser.broker_name";
    public static final String NEW_EG_NAME = "exerciser.new_eg_name";
    public static final String POLICY_NAME = "exerciser.policy_name";
    public static final String NEW_POLICY_NAME = "exerciser.new_policy_name";
    public static final String NEW_POLICY_CONTENTS = "exerciser.new_policy_contents";
    public static final String DEPLOYED_OBJECTS_TO_REMOVE = "exerciser.objects_to_remove";
    public static final String EG_NAME = "exerciser.eg_name";
    public static final String APPL_NAME = "exerciser.appl_name";
    public static final String LIB_NAME = "exerciser.lib_name";
    public static final String DEPLOY_WAIT_TIME_SECS = "exerciser.deploy_wait_time_secs";
    public static final String EG_CREATE_WAIT_TIME_SECS = "exerciser.eg_create_wait_time_secs";
    public static final String POLICY_WAIT_TIME_SECS = "exerciser.policy_wait_time_secs";
    public static final String SYNCHRONOUS_REQUESTS = "exerciser.synchronous_requests";
    public static final String SYNCHRONOUS_REQUESTS_ENABLED = "exerciser.synchronous_requests_enabled";
    public static final String SYNCHRONOUS_REQUESTS_DISABLED = "exerciser.synchronous_requests_disabled";
    public static final String SYNCHRONOUS_REQUESTS_HELP = "exerciser.synchronous_requests_help";
    public static final String OTHER_WAIT_TIME_SECS = "exerciser.other_wait_time_secs";
    public static final String ACTION_EXECUTE = "exerciser.action_execute";
    public static final String ACTION_EXECUTE_ACTION_NAME = "exerciser.action_execute_action_name";
    public static final String ACTION_EXECUTE_ACTION_PARAMETERS = "exerciser.action_execute_action_parameters";
    public static final String ACTION_EXECUTE_OBJECT_NAME = "exerciser.action_execute_object_name";
    public static final String ACTION_EXECUTE_OBJECT_PROPERTIES = "exerciser.action_execute_object_properties";

   /**
     * Creates a resource handler that uses the supplied filename
     * for obtaining resources.
     * @param bundleName filename of the resource to use. The resource
     * file is loaded using Java's resource bundle loader, which means that
     * a locale specific bundle will be attempted to be loaded before
     * a general one.
     */
    private ResourcesHandler(String bundleName) {
        try {
            resources = ResourceBundle.getBundle(bundleName);
            cacheAllResources();
        } catch (MissingResourceException ex) {
            System.err.println("Warning: Can't find resource bundle '"+bundleName+"'");
            resources = null;
        }
    }

    /**
     * Requests a resource from the bundle that contains localisable strings
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return String containing the value of the requested resource,
     * or an empty string if the resource could not be found.
     */
    public synchronized static String getNLSResource(String keyName) {
        if (nlsHandler == null) {
            nlsHandler = new ResourcesHandler(NLS_RESOURCE_FILENAME);
        }
        return nlsHandler.getResource(keyName, "");
    }

    /**
     * Requests a resource from the bundle that contains localisable strings
     * and substitutes any %n characters with the appropriate string
     * from the supplied array.
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return String containing the value of the requested resource,
     * or an empty string if the resource could not be found.
     */
    public synchronized static String getNLSResource(String keyName, String[] substitutions) {
        return MessageFormat.format(getNLSResource(keyName), (Object[])substitutions);
    }

    /**
     * Requests a resource from the bundle that contains user settings
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return String containing the value of the requested resource,
     * or null if the resource could not be found.
     */
    public synchronized static String getUserSetting(String keyName) {
        return getUserSetting(keyName, null);
    }

    /**
     * Requests a resource from the bundle that contains user settings
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return String containing the value of the requested resource,
     * or defaultValue if the resource could not be found.
     */
    public synchronized static String getUserSetting(String keyName, String defaultValue) {
        if (settingsHandler == null) {
            settingsHandler = new ResourcesHandler(SETTINGS_RESOURCE_FILENAME);
        }
        return settingsHandler.getResource(keyName, defaultValue);
    }

    /**
     * Requests a resource from the bundle that contains user settings
     * and returns the value as an int.
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return int containing the value of the requested resource,
     * or defaultValue if the resource could not be found.
     */
    public synchronized static int getUserSettingInt(String keyName, int defaultValue) {

        int retVal = defaultValue;
        String valueString = getUserSetting(keyName, null);
        if (valueString != null) {
            try {
                retVal = Integer.parseInt(valueString);
            } catch (NumberFormatException ex) {
                // ignore
            }
        }
        return retVal;
    }

    /**
     * Requests a resource from the bundle that contains user settings
     * and returns the value as an long.
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return long containing the value of the requested resource,
     * or defaultValue if the resource could not be found.
     */
    public synchronized static long getUserSettingLong(String keyName, long defaultValue) {

        long retVal = defaultValue;
        String valueString = getUserSetting(keyName, null);
        if (valueString != null) {
            try {
                retVal = Long.parseLong(valueString);
            } catch (NumberFormatException ex) {
                // ignore
            }
        }
        return retVal;
    }

    /**
     * Requests a resource from the bundle that contains user settings
     * and returns the value as an int.
     * @param keyName constant used to identify the requested resource
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return int containing the value of the requested resource,
     * or defaultValue if the resource could not be found.
     */
    public synchronized static boolean getUserSettingBoolean(String keyName, boolean defaultValue) {
        boolean retVal = defaultValue;
        String valueString = getUserSetting(keyName, null);
        if (valueString != null) {
            if (valueString.equals("true")) {
                retVal = true;
            } else {
                retVal = false;
            }
        }
        return retVal;
    }



    /**
     * Requests a resource from the current bundle
     * @param keyName Key describing the resource that is sought
     * @param defaultValue Value to return if the resource could not
     * be found
     * @return String containing the value of the requested resource,
     * or defaultValue if the resource could not be found.
     */
    private String getResource(String keyName, String defaultValue) {
        String retVal = null;
        if (resourcesCache != null) {
            retVal = resourcesCache.getProperty(keyName);
        }
        if ((retVal == null) && (resources != null)) {
            try {
                retVal = resources.getString(keyName);
            } catch (MissingResourceException ex) {
                // ignore - the default will be used
            }
        }

        if (retVal == null) {
            retVal = defaultValue;
        }
        return retVal;
    }

    /**
     * Attempts to modify the resource with the supplied key name
     * to the supplied value. The change is non-persistent until
     * saveUserSettings() is called.
     * @param newKey the key name to change - use a constant
     * from this file rather than a literal
     * @param newValue the new value
     *
     */
    public static void setUserSetting(String newKey, String newValue) {
        if (settingsHandler.resourcesCache == null) {
            settingsHandler.cacheAllResources();
        }
        if (!("".equals(newKey))) {
            settingsHandler.resourcesCache.put(newKey, newValue);
        }
    }

    /**
     * Saves the exerciser's user settings to a file
     */
    public static void saveUserSettings() {
        if (settingsHandler.resourcesCache == null) {
            settingsHandler.cacheAllResources();
        }

        File f = new File(SETTINGS_RESOURCE_FILENAME + ".properties");
        try {
            settingsHandler.resourcesCache.store(new FileOutputStream(f), " Configuration Settings saved by the IBM Integration API (CMP) Exerciser");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Places all of the resources inside the current bundle
     * and adds them into a hashtable in memory.
     */
    private void cacheAllResources() {
        if (resourcesCache == null) {
            resourcesCache = new Properties();
        }
        if (resources != null) {
                Enumeration<String> e = resources.getKeys();
                while (e.hasMoreElements()) {
                    String key = e.nextElement();
                    String value = resources.getString(key);
                    resourcesCache.put(key,value);
                }
        }
    }

    /**
     * Retrieves the boolean resource with the supplied key name
     * and sets it to true if the current value is false, or to false
     * if the current value is true.
     * The user settings file is then saved to reflect the change.
     * @param keyName Key describing the setting that is required
     * @param newValueIfOldValueUnknown The new value of the setting
     * if the current value is unknown.
     * @return newValue the new value of the setting
     *
     */
    public static boolean toggleUserSettingBoolean(String keyName, boolean newValueIfOldValueUnknown) {
        boolean newValue = !(getUserSettingBoolean(keyName, !newValueIfOldValueUnknown));
        setUserSetting(keyName, ""+newValue);
        return newValue;
    }


}

