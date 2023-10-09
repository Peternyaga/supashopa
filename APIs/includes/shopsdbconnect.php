<?php

class shopsdbconnect{
private $con;
	function __construct()
	{
		# code...
	}

	function connect(){
$servername="localhost";
 $username= "root";
 $password= "";
 $databasename= "db_supashopa";
 		$this->con=new mysqli($servername, $username, $password, $databasename);

		if (mysqli_connect_errno()) {
			echo "failed to connect with db_shops";
		}
			//echo "connection to database was successful";
		

		return $this->con;
	}
	
}