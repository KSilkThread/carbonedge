import yaml

RELATIVE_PATH_TO_MINIFAB_MODULE = "./../../minifabric"

class yamlChange:
    def __init__(self, relativePathToYaml, identificationAttribute, identificationValue, changeAttribute, changeValue ):
        self.relativePathToYaml = relativePathToYaml
        self.identificationAttribute = identificationAttribute
        self.identificationValue = identificationValue
        self.changeAttribute = changeAttribute
        self.changeValue = changeValue

#only usable if its a sequence and the attribute that we look for are on the first layer 
def over(yamlChange : yamlChange):
    
    # Open the YAML file
    with open(yamlChange.relativePathToYaml, "r") as f:
        # Load the contents of the file into a Python object
        data = yaml.safe_load(f)

    # Find the serialization of the part that has to be changed
    for serialization in data:
        if serialization[yamlChange.identificationAttribute] == yamlChange.identificationValue:
            # Modify the specific attribute with the new value
            serialization[yamlChange.changeAttribute] = yamlChange.changeValue
            break

    # Serialize the object to YAML format
    yaml_data = yaml.dump(data)

    # Write the serialized data back to the file, overwriting the original contents
    with open(yamlChange.relativePathToYaml, "w") as f:
        f.write(yaml_data)

def processing(d, yamlChange : yamlChange, listPOS: int):
    #l: list, l2:list, addAttribute, addValue
    # Iterate through the key-value pairs in the dictionary
    if isinstance(d, list):
        for serialization in d:
            # going one step deeper in a key : value pair where the value pair is a dict
            if serialization[yamlChange.identificationAttribute[listPOS]] == yamlChange.identificationValue[listPOS] and yamlChange.identificationValue[listPOS] == None:
                    # knowledge that in this value should be another serialization
                    processing(yamlChange.identificationAttribute[listPOS],yamlChange, listPOS+1)
                    break    
            # Finishing Touch in a list 
            if serialization[yamlChange.identificationAttribute[listPOS]] == yamlChange.identificationValue[listPOS]:
                if listPOS == (len(yamlChange.identificationAttribute)-1):
                    serialization[yamlChange.changeAttribute] = yamlChange.changeValue 
                    break
                processing(serialization, yamlChange, listPOS+1)   
    if isinstance(d, dict):
        for key, value in d.items():
            if key == yamlChange.identificationAttribute[listPOS] and yamlChange.identificationValue[listPOS] == None:
                if listPOS == (len(yamlChange.identificationAttribute)-1):
                    break
                processing(value,yamlChange,listPOS+1)
            if key == yamlChange.identificationAttribute[listPOS] and value==yamlChange.identificationValue[listPOS]:
                if listPOS == (len(yamlChange.identificationAttribute)-1):
                    d[yamlChange.changeAttribute[listPOS]] = yamlChange.changeValue[listPOS]
                process_dict(value, yamlChange,listPOS+1)
        
           
def overwriteYAML(yamlChange : yamlChange):
     # Open the YAML file
    with open(yamlChange.relativePathToYaml, "r") as f:
        # Load the contents of the file into a Python object
        data = yaml.safe_load(f)

    processing(data,yamlChange,0)

    # Serialize the object to YAML format
    yaml_data = yaml.dump(data)

    # Write the serialized data back to the file, overwriting the original contents
    with open(yamlChange.relativePathToYaml, "w") as f:
        f.write(yaml_data)







def process_dict(d: dict, l: list, addAttribute, addValue, listPOS: int):
    # Iterate through the key-value pairs in the dictionary
    for key, value in d.items():
        # adding a new mapping as soon as we are at the mapping we want to be
        if listPOS == (len(l)):
            d[addAttribute] = addValue
            break
        if key == l[listPOS]:
            process_dict(value, l,addAttribute,addValue,listPOS+1)

#This function is only usabel, if identificationAttribute is using a list 
# that gives the mappings towards the place for the addition of the new key:value pair
def addMappingToYaml(yamlChange : yamlChange):
    # Read the YAML file into a Python dictionary
    with open(yamlChange.relativePathToYaml, 'r') as f:
        fabric = yaml.safe_load(f)
    # Add an additional mapping to the 'peer' dictionary
    # trough itertation of list of mapping
    if isinstance(yamlChange.identificationAttribute, list):
        process_dict(fabric,yamlChange.identificationAttribute,yamlChange.changeAttribute,yamlChange.changeValue, 0)
    # Write the modified dictionary back to the YAML file
    with open(yamlChange.relativePathToYaml, 'w') as f:
        yaml.safe_dump(fabric, f)