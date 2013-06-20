<!DOCTYPE html>
<html lang="en" ng-app="Home">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Welcome to UrbanMovers</title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <p><em>Breve testo che spiega le motivazioni della nostra iniziativa, magari messo gi&ugrave; secondo il video why-how-what</em></p>
        <p>Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
        <p>
            <div class="video">
                <em>Video di presentazione</em>
            </div>
        </p>
        <p><em>Richiesta email per aggiornamenti</em></p>
        <p>Se interessati lasciare mail qui sotto</p>
        <div ng-controller="MailCollector">
            <form ng-submit="collectMail()">
                <input type="text" ng-model="emailAddress" placeholder="insert your email address">
                <input class="button" type="submit" value="ok">
            </form>
            <span>{{greetings}}</span>
        </div>
        <br />
        <br />
        <!--<a class="button" href="<?php echo \base_url()?>register/register"> Register</a>-->
        <?php $this->load->view('templates/footer'); ?>
        <script>angular.module("Home", []).value("base_url", "<?php echo base_url() ?>")</script>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/mailCollector.js"></script>
    </body>
</html>