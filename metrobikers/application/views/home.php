<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Welcome to UrbanMovers</title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <p>Hello Urban Mover!</p>
        <a href="<?php echo \base_url()?>register/register"> Register</a>
        <?php $this->load->view('templates/footer'); ?>
    </body>
</html>