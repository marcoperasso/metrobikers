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
            <?php $url = base_url("user/connect?userkey=" . urlencode($validationkey)) . '&inviteduserid=' . $user_contacted->id; ?>
            <p>Ciao <?php echo $user_contacted->name ?>, <?php echo $user->to_string(); ?> 
                desidera includerti nel suo gruppo.</p>
            <p>Utilizza questo link: <a href="<?php echo $url; ?>"><?php echo $url; ?></a> per accettare.</p>

        </div>
        <div class="col-md-1"></div>
    </div>
</html>