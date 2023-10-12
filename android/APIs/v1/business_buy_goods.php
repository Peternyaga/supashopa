<?php
$tokenURL="https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
$credentials= base64_encode("CVckGrldn5SCKxkjxtUvXQQu6tgCgeBX". ":". "NGl6j1m6AlZq1kVn");
$ch=curl_init($tokenURL);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Basic ' . $credentials]);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$response=curl_exec($ch);
curl_close($ch);
echo($response);
$response=json_decode($response);
$token=$response->{'access_token'};

echo ("Token= ". $token. "\n");

$buyGoodsURL="https://sandbox.safaricom.co.ke/mpesa/b2b/v1/paymentrequest";

$curl_post_data=array( 
	'Initiator'=>"testapi",
  	'SecurityCredential'=>"REMOVED_ROTATE_CREDENTIAL",
  	'CommandID'=> "BusinessPayBill",
  	'SenderIdentifierType'=> "4",
  	'RecieverIdentifierType'=>"4",
  	'Amount'=>"239",
  	'PartyA'=>"600990",
  	'PartyB'=>"600000",
  	'AccountReference'=>"174379",
  	'Requester'=>"254700000000",
  	'Remarks'=>"OK",
  	'QueueTimeOutURL'=>"https://mydomain.com/b2b/businessbuygoods/queue/",
  	'ResultURL'=>"https://mydomain.com/b2b/businessbuygoods/result/"
  );
$payload=json_encode($curl_post_data);
//echo $payload;
$ch= curl_init($buyGoodsURL);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer '.$token, 'Content-Type: application/json']);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$result=curl_exec($ch);
echo($result);


?>