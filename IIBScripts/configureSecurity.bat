@ECHO OFF
set BROKER=IB9NODE
set EG=default

IF "%MQSI_VERSION%"=="" GOTO :ERROR

echo "Criando Security Profile default..."
mqsicreateconfigurableservice %BROKER% -c SecurityProfiles -o DefaultSecurityProfile -n authentication,authenticationConfig,authorization,authorizationConfig,propagation,rejectBlankpassword -v "NONE,\"\",NONE,\"\",TRUE,TRUE"
echo "Criando Security Profile DefaultService_SP..."
mqsicreateconfigurableservice %BROKER% -c SecurityProfiles -o DefaultService_SP -n authentication,authenticationConfig,authorization,authorizationConfig,propagation,rejectBlankpassword -v "LDAP,\"ldap://ldapserver.amil.com.br:389/DC=grupoamil,dc=com,dc=br?SAMAccountName\",LDAP,\"ldap://ldapserver.amil.com.br:389/CN=G-WIKI-SOA,OU=wiki,OU=adm_aplicacoes,OU=Geral,OU=ALPHAVILLE,OU=SAOPAULO,OU=Sites,DC=grupoamil,DC=com,DC=br\",TRUE,TRUE"
echo "Associando usuário de bind para o LDAP..."
mqsisetdbparms %BROKER% -n ldap::ldapserver.com.br -u admiib_bind -p senha
REM http://pic.dhe.ibm.com/infocenter/wmbhelp/v8r0m0/topic/com.ibm.etools.mft.doc/ac60370_.htm
echo "Criando Policy Set e Policy Set Binding..."
mqsicreateconfigurableservice %BROKER% -c PolicySets -o DefaultPolicySet
mqsicreateconfigurableservice %BROKER% -c PolicySetBindings -o DefaultPolicySetBinding
mqsichangeproperties %BROKER% -c PolicySets -o DefaultPolicySet -n ws-security -p DefaultPolicySet.xml
mqsichangeproperties %BROKER% -c PolicySetBindings -o DefaultPolicySetBinding -n ws-security -p DefaultPolicySetBinding.xml
mqsichangeproperties %BROKER% -c PolicySetBindings -o DefaultPolicySetBinding -n associatedPolicySet -v DefaultPolicySet
echo "É necessário reiniciar o EG que utiliza os policy sets..."
echo "Reiniciando o execution group: %EG%"
mqsireload %BROKER% -e %EG%
Exit /B 0
:ERROR
echo "Este script deve ser executado no "Command Console" do Broker
pause
Exit /B 5
