# Excel2Json
Converts an Excel document to a JSON document. Well kinda, you need to save the Excel into a CSV format first :)


## Usage
java -jar excel2json {full_path_to_csv_file}

Then copy paste the output from stdout

## Requirements
Maven  
Java 1.8  

## Building
mvn clean install

## Example Excel format 
Save this to CSV  
![alt text](https://github.com/akang/Excel2Json/blob/master/excel_image.png)


## Example CSV format
root,,,  
,node1,,  
,,node2,  
,,node3,  
,,node4,  
,,node5,  
,,node6,  
,node7,,  

## JSON Output
```json
{
	"root": {
		"option1": {
			"option2": "",
			"option3": "",
			"option4": "",
			"option5": "",
			"option6": ""
		},
		"option7": ""
	}
}
```




