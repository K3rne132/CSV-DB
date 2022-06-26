# CSV DB
## Description
CSV DB is a simple database that provides the following functions:
- loading any kind of CSV file
- saving the loaded CSV file to a file in XML and JSON format
- analysis of the loaded data using the pseudo-syntax of the SQL language

To perform database operations, you must use a shell. The interface is text-based and allows you to enter commands in a language similar to SQL.

## Content
|File|Description|
|-|-|
|**Database.java**|It contains information about the read data (records, columns) and performs operations on them|
|**Log.java**|Provides a function to save errors to the **.log** file|
|**Record.java**|Represents a single record (row) in the database|
|**RecordAscComparator.java**|Comparator for ascending String values|
|**RecordDescComparator.java**|Comparator for descending String values|
|**RecordAscComparatorInteger.java**|Comparator for ascending Integer values|
|**RecordDescComparatorInteger.java**|Comparator for descending Integer values|
|**Shell.java**|It provides an interface through which the user communicates with the program|
|**SQL.java**|Parses the command line and saves it into a program-friendly form|

## Pseudo-SQL
The commands in the shell are as follows: the user supplies a keyword and assigns arguments to it. The number of arguments is strictly defined and depends on the keyword. Keywords are case-insensitive. In order to write whitespace it is recommended (required in case of spaces) to write them in quotation marks (").

## Loading and saving
After starting the shell, you will notice: ```Database> ``` which means that no data has been loaded yet. In order to load them, we use the command:
```READ file delimiter```
For example:
```read "data file.csv" ","```
In the case of correctly loaded data, we will notice a change to ```data file.csv> ``` and information about the number of loaded columns and records.

In order to save the previously read data from a CSV file to JSON or XML format, use the command:
```WRITE xml/json file```
For example:
```write xml "saved file.xml"```
```write json "saved file.json"```

## Pseudo-SQL Keywords
### SELECT
**SELECT** selects any columns from the database and lists them. The syntax for the keyword SELECT is as follows:
```SELECT column1 column2 column3 ...```

#### Special words
**SELECT \*** - selects all columns and must be written as **first argument** of SELECT (rest of arguments are ignored)

#### Example
```SELECT a b c d``` - selects columns: a, b, c, d
```SELECT "a" select qwerty Select """ sELecT "col umn"``` - selects columns named: a, qwerty, ", col umn

### WHERE
**WHERE** filters the data retrieved from SELECT. The word WHERE syntax is as follows:
```WHERE column <operator> value <conjunction> column <operator> value <conjunction> ...```
If you use conjunctions, remember that the logical expression is read from left to right:
```(((((expr1) and expr2) or expr3) or expr4) and expr5)```
and it is not possible to use parentheses at this time.


#### Operators and conjunctions
**Operator =** -  checks that the String value in the specified column is equal to the given String value
**Operator >** -  checks that the String value in the specified column is greater than the given String value
**Operator <** -  checks that the String value in the specified column is less than the given String value
**Operator >=** - checks if the String value in the specified column is greater than or equal to the given String value
**Operator <=** - checks if the String value in the specified column is less than or equal to the given String value

For the following operators, it is checked whether both values are integers:
**Operator i=** -  checks that the Integer value in the specified column is equal to the given Integer value
**Operator i>** -  checks that the Integer value in the specified column is greater than the given Integer value
**Operator i<** -  checks that the Integer value in the specified column is less than the given Integer value
**Operator i>=** - checks if the Integer value in the specified column is greater than or equal to the given Integer value
**Operator i<=** - checks if the Integer value in the specified column is less than or equal to the given Integer value

**Conjunction and** - logical AND operator
**Conjunction or** - logical OR operator

#### Example
```SELECT * WHERE a i>= 20 and b < b or c = "qwerty"``` - selects all records which: have the numeric value of column a greater than or equal to 20 and simultaneously the value of column b must be alphabetically less than "b" or the value of column c is "qwerty".

### ORDERBY
**ORDERBY** arranges the data in a given order in relation to the selected one column. It has the following syntax:
```ORDERBY column (i)asc/(i)desc```

#### Słowa specjalne
**ASC order** - arranges records alphabetically in ascending order relative to the column
**DESC order** - arranges records alphabetically in descending order relative to the column
**IASC order** - orders the records numerically in ascending order relative to the column
**IDESC order** - orders the records numerically in descending order relative to the column
For integer operators, it is checked whether the values in the column are integers.

#### Example
```SELECT * ORDERBY a asc``` - orders the values in column a in alphabetical order
```SELECT * ORDERBY b idesc``` - orders the values in column b in descending order of the integer order

### GROUPBY
The keyword is not implemented in the current version. It has no effect on the behavior of the program.