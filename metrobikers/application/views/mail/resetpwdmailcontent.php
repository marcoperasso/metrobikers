<link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/base/jquery-ui.css" type="text/css" rel="stylesheet"/>
<link href="<?php echo base_url();?>asset/css/ecommuters.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>

<meta http-equiv="Content-type" content="text/html;charset=UTF-8">


<html>

<div class="col-md-3"></div>

<div class="col-md-6">
    <div class="container">
        <?php $url = base_url("register/preactivate?resetpwd=true&userkey=" . urlencode($validationkey));?>
        <h2 class="text-center">Ripristino password</h2><br />
        <p>Ciao <?php echo $user_draft->name;?>, abbiamo ricevuto la tua richiesta di ripristino password.</p>
        <p>Utilizza questo link: <a href="<?php echo $url;?>"><?php echo $url;?></a> per completare la procedura.</p>

        <div class="col-md-2">

        </div>

        <div class="col-md-8">

        </div>
        <div class="col-md-2">

        </div>
    </div>
</div>
<div class="col-md-3"></div>
</html>