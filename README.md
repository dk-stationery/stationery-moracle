# stationery-moracle
aggregate text file (csv) using sql,

# quick start
1. mvn clean package -DskipTests
2. cd ./stationery-moracle-jdbc/target
3. cp stationery-moracle-jdbc-1.0-SNAPSHOT.jar #SQUIRREL LIB FOLDER#
4. SQUIRREL-SQL setting   
                  url : moracle://inputPath=/Users/kun7788/Desktop/input/&seperator=,&fileExtension=.csv&fileEncoding=MS949&isHeader=Y    
                  driver classname : org.tommy.stationery.moracle.jdbc.MoracleDriver     
