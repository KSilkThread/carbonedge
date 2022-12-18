 # Open the template file in read mode
 #Overwrite with string
RELATIVE_PATH_TO_MINIFAB = "./../../minifabric"
 
with open(f"{RELATIVE_PATH_TO_MINIFAB}/playbooks/ops/netup/k8stemplates/allnodes.j2.", 'r') as f:
    # Read the template into a string
    template_string = f.read()

# Replace the old image with the new image
# line 92
template_string = template_string.replace('hyperledger/fabric-ca:1.4', 'laughingadversial/fabric-ca:arm64-1.5.5')
# line 255
template_string = template_string.replace('hyperledger/fabric-orderer:{{ fabric.release }}', 'laughingadversial/fabric-orderer:3.0')
# line 366
template_string = template_string.replace('hyperledger/fabric-couchdb:latest', 'couchdb:3.2.2')
# line 538
template_string = template_string.replace('hyperledger/fabric-peer:{{ fabric.release }}', 'laughingadversial/fabric-peer:3.0')

# Open the template file in write mode
with open(f"{RELATIVE_PATH_TO_MINIFAB}/playbooks/ops/netup/k8stemplates/allnodes.j2.", 'w') as f:
    # Write the modified template string to the file
    f.write(template_string)

