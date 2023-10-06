<?php
require_once "../includes/dbOperations.php";
$response=array();
$items=array();
$item=array();

if ($_SERVER["REQUEST_METHOD"]=='POST') {

	if(isset($_POST['shop_id'])&&isset($_POST['userId'])&&isset($_POST['total'])&&isset($_POST['items'])&&isset($_POST['shop'])){
		$db=new dbOperations($_POST['shop']);
		$orderId=$db->placeOrder($_POST['shop_id'], $_POST['userId'], $_POST['total'], $_POST['session_id'], $_POST['payment_status'],"transaction was successful", $_POST['mpesaReceiptID']);
		//echo $orderId['id'];
		$items=json_decode(($_POST['items']),true);

		if (!$orderId==false) {
			for ($i=0; $i < count($items); $i++) { 
				$item=($items[$i]);
				$itemId=$item["item id"];
				$quantity=$item["quantity"];
				$response=$db->orderItems($_POST['shop_id'], $orderId['id'], $itemId, $quantity);
			}
		}else{
			$response['code']=2;
			$response["error"]=true;
			$response["message"]="Order could not be added to database";
	}
		
		
	}else{
		$response['code']=3;
		$response["error"]=true;
		$response["message"]="Required fields missing";
	}

}else{
	$response['code']=4;
$response["error"]=true;
$response["message"]="invalid REQUEST!";
}
echo json_encode($response);
?>