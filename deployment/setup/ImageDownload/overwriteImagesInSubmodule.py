import yaml 
import yamlOverwrite as yA
import jinja2Overwrite as j2


RELATIVE_PATH_TO_MINIFAB_MODULE = "./../../minifabric"

def main():
    # code here
    # 13 changes needed referenced to https://github.com/LIONS-DLT/minifabric/commit/810fc1177c9d4cf4ef1adf678ee3cf42f1325ea8

    #/playbooks/ops/certgen/createcerts.yaml changes
    changesForARM1 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/certgen/createcerts.yaml","name",'''Use fabric tools container to create channel artifacts''',
    "command",'''>-
    docker run --rm --name tools
    -v /var/run/:/host/var/run
    -v {{ hostroot}}/vars:/etc/hyperledger/fabric
    laughingadversial/fabric-tools:3.0 /etc/hyperledger/fabric/run/certtxgen.sh''')
   
    #/playbooks/ops/consoleup/apply.yaml changes
    changesForARM2 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/consoleup/apply.yaml","name","Start configtxlator","command",'''>-
    docker run -d --network {{ NETNAME }} --name configtxlator.{{ NETNAME }} --hostname configtxlator.{{ NETNAME }}
    laughingadversial/fabric-tools:3.0
    /bin/bash -c "/usr/local/bin/configtxlator start --CORS=*"''')

    #/playbooks/ops/netup/dockerapply.yaml changes 
    changesForARM3 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/netup/dockerapply.yaml","name","Start couchdb nodes if db type is set to couchdb","command",'''>-
    docker run -d --network {{ NETNAME }} --name {{ item.fullname }} {{ item.portmap }}
    -e COUCHDB_USER=admin -e COUCHDB_PASSWORD={{ item.adminPassword }}
    -v {{ item.fullname }}:/opt/couchdb/data
    {{ container_options }}
    --hostname {{ item.fullname }} couchdb:3.2.2''')
    
    changesForARM4 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/netup/dockerapply.yaml","name","Start all peer nodes","command",'''>-
    docker run -d --network {{ NETNAME }} --name {{ item.fullname }} --hostname {{ item.fullname }}
    --env-file {{ pjroot }}/vars/run/{{ item.fullname }}.env {{ item.portmap }}
    -v /var/run/:/host/var/run
    -v {{ mpath }}/{{item.org}}/peers/{{item.fullname}}/msp:/etc/hyperledger/fabric/msp
    -v {{ mpath }}/{{item.org}}/peers/{{item.fullname}}/tls:/etc/hyperledger/fabric/tls
    -v {{ item.fullname }}:/var/hyperledger/production
    {{ container_options }}
    laughingadversial/fabric-peer:3.0 peer node start''')

    changesForARM5 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/netup/dockerapply.yaml","name","Start all orderer nodes","command",'''>-
    docker run -d --network {{ NETNAME }} --name {{ item.fullname }} --hostname {{ item.fullname }}
    --env-file {{ pjroot }}/vars/run/{{ item.fullname }}.env {{ item.portmap }}
    -v {{ hostroot }}/vars/genesis.block:/var/hyperledger/orderer/orderer.genesis.block
    -v {{ mpath }}/{{item.org}}/orderers/{{item.fullname}}/msp:/var/hyperledger/orderer/msp
    -v {{ mpath }}/{{item.org}}/orderers/{{item.fullname}}/tls:/var/hyperledger/orderer/tls
    -v {{ item.fullname }}:/var/hyperledger/production/orderer
    {{ container_options }}
    laughingadversial/fabric-orderer:3.0''')

    changesForARM6 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/netup/dockerapply.yaml","name","Start all ca nodes","command",'''>-
    docker run -d --network {{ NETNAME }} --name {{ item.fullname }} --hostname {{ item.fullname }}
    --env-file {{ pjroot }}/vars/run/{{ item.fullname }}.env {{ item.portmap }}
    -v {{ hostroot }}/vars/keyfiles/{{ orgattrs[item.org].certpath }}/{{item.org}}:/certs
    -v {{ item.fullname }}:/etc/hyperledger/fabric-ca-server
    {{ container_options }}
    laughingadversial/fabric-ca:arm64-1.5.5 {{ item.command }}''')

    changesForARM7 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/netup/dockerapply.yaml","name","Start cli container for all fabric operations","command",'''>-
    docker run -dit --network {{ NETNAME }} --name {{ CLINAME }} --hostname {{ CLINAME }}
    -v /var/run/docker.sock:/var/run/docker.sock
    -v {{ hostroot }}/vars:/vars
    -v {{ hostroot }}/vars/chaincode:{{ gopath }}/src/github.com/chaincode
    {{ container_options }}
    laughingadversial/fabric-tools:3.0''')

    #/playbooks/ops/netup/k8sapply.yaml changes
    changesForARM8 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/playbooks/ops/netup/k8sapply.yaml","name","start cli container","command",'''>-
      docker run -dit --network {{ NETNAME }} --name {{ CLINAME }} --hostname {{ CLINAME }}
      -v /var/run/docker.sock:/var/run/docker.sock
      -v {{ hostroot }}/vars:/vars
      -v {{ hostroot }}/vars/chaincode:{{ gopath }}/src/github.com/chaincode
      -e https_proxy={{ https_proxy }} -e no_proxy={{ no_proxy }}
      {{ container_options }}
      laughingadversial/fabric-tools:3.0''')

    
    yA.overwriteYAML(changesForARM1)
    yA.overwriteYAML(changesForARM2)
    yA.overwriteYAML(changesForARM3)
    yA.overwriteYAML(changesForARM4)
    yA.overwriteYAML(changesForARM5)
    yA.overwriteYAML(changesForARM6)
    yA.overwriteYAML(changesForARM7)
    yA.overwriteYAML(changesForARM8)

    #/spec.yaml additions to mapping
    changesForARM9 = yA.yamlChange(f"{RELATIVE_PATH_TO_MINIFAB_MODULE}/spec.yaml",["fabric","settings","ca"],[],"CORE_CHAINCODE_BUILDER",'''laughingadversial/fabric-ccenv:3.0''')

    yA.addMappingToYaml(changesForARM9)
   
    #playbooks/ops/netup/k8stemplates/allnodes.j2
    #
    #.j2 file....;
    #four further changes, change 9 to 13
    # Replace the old image with the new images
    # line 92
    # line 255
    # line 366
    # line 538
    
    filterList = []
    
    filterList.append(('hyperledger/fabric-ca:1.4', 'laughingadversial/fabric-ca:arm64-1.5.5')) 
    filterList.append(('hyperledger/fabric-orderer:{{ fabric.release }}', 'laughingadversial/fabric-orderer:3.0')) 
    filterList.append(('hyperledger/fabric-couchdb:latest', 'couchdb:3.2.2')) 
    filterList.append(('hyperledger/fabric-peer:{{ fabric.release }}', 'laughingadversial/fabric-peer:3.0')) 
    
    j2.overwriteAllnodes(RELATIVE_PATH_TO_MINIFAB_MODULE, filterList)
     


if __name__ == "__main__":
    main()

