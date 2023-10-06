<?php

/**
* 
*/
class DBOperations
{
	private $response=array();
	private $con;
	private $db_name;
	function __construct($db_name)
	{
		$this->db_name=$db_name;
		require_once dirname(__FILE__).'/dbConnect.php';
		$db=new dbConnect($db_name);
		$this->con=$db->connect();
	}
	
	public function createUser($user_id,$phone,$first_name,$last_name,$address,$city_town,$residence,$email,$password){
		$password=md5($password);
		$stmt=$this->con->prepare("INSERT INTO `tbl_users`(`user id`, `first name`, `last name`, `phone number`, `email`, `password`) VALUES (null,?,?,?,?,?)");
		$stmt->bind_param("sssss",$first_name,$last_name,$phone,$email,$password);
		if($stmt->execute()){
			return true;
		}else{
			return false;
		}
	}

	public function login($phone, $pass){
		$password=md5($pass);
		$stmt=$this->con->prepare("SELECT * FROM `tbl_users` WHERE `phone number`=? && `password`=?");
		$stmt->bind_param("ss", $phone,$password);
		$stmt->execute();
		$stmt->store_result();
		return $stmt->num_rows>0;
	}

	public function userExists($phone, $email){
		$stmt=$this->con->prepare("SELECT * FROM `tbl_users` WHERE `phone number`= ?||`email`= ?");
		$stmt->bind_param("ss",$phone, $email);
		$stmt->execute();
		$result=$stmt->get_result();
		return $result->fetch_assoc();

	}

	public function getitemsfromdb($shopId){

		$stmt=$this->con->prepare("SELECT * FROM `tbl_products` WHERE `shop code`=? ORDER BY `category` DESC");
		$stmt->bind_param("i", $shopId);
		$stmt->execute(); 
		$result=$stmt->get_result();
		$rowcount=0;
		$rows=array();

		while ($rowcount<$result->num_rows) {
			$rows[$rowcount]=$result->fetch_assoc();
			$rowcount+=1;
		}
	
		return ($rows);

	}

	public function searchItems($shopId, $query){
		$stmt=$this->con->prepare('SELECT * FROM `tbl_products` WHERE `shop code` = ? AND `name` LIKE ? ORDER BY `category`');
		$stmt->bind_param("ss",$shopId, $query);
		$stmt->execute();
		$result=$stmt->get_result();
		$rows=array();
		$count=0;
		while ($count<$result->num_rows) {
			$rows[$count]=$result->fetch_assoc();
			$count+=1;
			# code...
		}
		return ($rows);
	}

	public function placeOrder($shopId, $userId, $total, $sessionId, $paymentStatus,$paymentResultDescription, $mpesaReceiptId){
		$stmt=$this->con->prepare("INSERT INTO `tbl_orders` (`shop id`, `user_id`, `session_id`, `total`, `payment status`, `payment result desc`, `mpesa receipt id`) VALUES (?, ?, ?, ?, ?, ?, ?)");
		$stmt->bind_param("sssssss",$shopId, $userId, $sessionId, $total, $paymentStatus, $paymentResultDescription, $mpesaReceiptId);
		$result=$stmt->execute();
		//echo $result;
		if ($result==true) {
			$stmt2=$this->con->prepare("SELECT `id` FROM `tbl_orders` WHERE `session_id`=?");
			$stmt2->bind_param("s",$sessionId);
			$stmt2->execute();
			$user=$stmt2->get_result();
			return $user->fetch_assoc();
		}else{
			return false;
		}
		
	}

	public function orderItems($shopId, $orderId, $itemId, $quantity){
		$stmt=$this->con->prepare("INSERT INTO `tbl_order_items` (`shop id`, `order_id`, `item_id`, `quantity`) VALUES (?, ?, ?, ?)");
		if ($stmt!=false){
			$stmt->bind_param("siss",$shopId, $orderId, $itemId, $quantity);
			if ($stmt->execute()) {
				$response['code']=0;
				$response['error']=false;
				$response['message']="your order has been received";
			}else{
				$response['code']=1;
				$response['error']=true;
				$response['message']="failed! Please try again";
			}

		}else{
			$response['code']=2;
			$response['error']=true;
			$response['message']="connection failed! Please try again";
		}
		return $response;
	}

	public function updateOrder($sessionId, $paymentStatus,$paymentResultDescription, $mpesaReceiptId){
		$stmt=$this->con->prepare("UPDATE `tbl_orders` SET `payment status`=?,`payment result desc`=?,`mpesa receipt id`=? WHERE `session_id`=?");
		$stmt->bind_param("ssss", $paymentStatus, $paymentResultDescription, $mpesaReceiptId, $sessionId);
		$result=$stmt->execute();
		return $result;

	}

	public function checkOrderStatus($sessionId){
		$stmt=$this->con->prepare("SELECT `payment status`FROM `tbl_orders` WHERE `session_id`=?");
		$stmt->bind_param("s", $sessionId);
		$stmt->execute();
		$result=$stmt->get_result();
		return $result->fetch_assoc();

	}

	public function searchForShop($query){
		$stmt=$this->con->prepare("SELECT * FROM `tbl_shops` WHERE `shop name` LIKE ?");
		$stmt->bind_param("s", $query);
		$stmt->execute();
		$result=$stmt->get_result();
		if ($result->num_rows>0) {
			for ($i=0; $i <$result->num_rows ; $i++) { 
				$response[$i]=$result->fetch_assoc();
			}
			return $response;
		}
		
	}
	
}
