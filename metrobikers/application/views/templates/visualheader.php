<?php
$user = get_user();
if ($user == NULL) {
        redirect('/');
} ?>
<div class="row">
    <div class="col-md-3">

    </div>
    <div class="col-md-6">
        <a href="/" title="Vai alla pagina principale">
            <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo" class="logo">
        </a>
    </div>
    <div class="col-md-3">
        <span>Benvenuto, <?php echo $user->to_string(); ?></span>
        <a class ="logoffcommand" href="javascript:void(0)" title="Esci">
            <img class="headercommand" src="<?php echo base_url() ?>asset/img/exit.png" alt="Esci"/>
        </a>
    </div>
</div>