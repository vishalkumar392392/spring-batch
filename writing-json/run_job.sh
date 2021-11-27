CURRENT_DATE=`date '+%Y/%m/%d'`
LESSON=$(basename $PWD)
mvn clean package -Dmaven.test.skip=true;
java -jar ./target/writing-json-0.0.1-SNAPSHOT.jar "run.date(date)=2021/02/02" "lesson=LESSON";
read;
