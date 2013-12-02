<link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/base/jquery-ui.css" type="text/css" rel="stylesheet"/>
<link href="<?php echo base_url('asset/css/ecommuters.css'); ?>" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>

<meta http-equiv="Content-type" content="text/html;charset=UTF-8">

<html>
    <div class="row">
        <div class="col-md-1"></div>
        <div class="col-md-4 center-text" >
            <img class="logo" src="<?php echo base_url('asset/img/logosmall.png'); ?>">
        </div>

        <div class="col-md-6 container">
            <p>Ciao <?php echo $user->name;?>, tu e <?php echo $user_contacted->name . ' ' . $user_contacted->surname; ?> 
                siete adesso nello stesso gruppo.</p>
            <p><a href="<?php echo base_url('user/my_ecommuters'); ?>">Visualizza il tuo gruppo</a></p>

        </div>
        <div class="col-md-1"></div>
    </div>
</html>