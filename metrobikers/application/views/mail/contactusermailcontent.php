<link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/base/jquery-ui.css" type="text/css" rel="stylesheet"/>
<link href="<?= base_url(); ?>asset/css/ecommuters.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>

<meta http-equiv="Content-type" content="text/html;charset=UTF-8">


<html>

    <div class="col-md-3"></div>

    <div class="col-md-6">
        <div class="container">
            <?php $url = base_url("user/connect?userkey=" . urlencode($validationkey)); ?>
            <h2 class="text-center">Entra nel gruppo di <?= $user->name . ' ' . $user->surname?></h2><br />
            <p>Ciao <?= $user_contacted->name ?>, <?= $user->name . ' ' . $user->surname?> 
                desidera includerti nel suo gruppo.</p>
            <p>Utilizza questo link: <a href="<?= $url ?>"><?= $url ?></a> per accettare.</p>

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