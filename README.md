# OBP-Scala-South

This project is a part of [OpenBankProject](https://github.com/OpenBankProject) and it is usually reffered as South side. It receives various requests from [OBP-API](https://github.com/OpenBankProject/OBP-API), usually reffered as North side. 
Based on received requests and local configuration it communicates with variuos data sources (provided by banks or local files etc.)
and returns gathered data back to North side in appropriate form. 

As a messaging midleware [Kafka](http://kafka.apache.org/downloads.html) is used. 
