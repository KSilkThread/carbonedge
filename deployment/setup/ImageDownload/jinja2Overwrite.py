 # Open the template file in read mode
 #Overwrite with string

def overwriteAllnodes(RELATIVE_PATH_TO_MINIFAB, l :list):
    with open(f"{RELATIVE_PATH_TO_MINIFAB}/playbooks/ops/netup/k8stemplates/allnodes.j2", 'r') as f:
        # Read the template into a string
        template_string = f.read()
    
    for i, (a , b) in enumerate(l):
        template_string = template_string.replace(a,b)
        
    # Open the template file in write mode
    with open(f"{RELATIVE_PATH_TO_MINIFAB}/playbooks/ops/netup/k8stemplates/allnodes.j2", 'w') as f:
        # Write the modified template string to the file
        f.write(template_string)

