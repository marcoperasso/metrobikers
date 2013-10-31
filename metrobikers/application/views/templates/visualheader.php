<div class="header">
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <a class="navbar-brand" href="/">ECOmmuters</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse navbar-ex1-collapse">
            <?php if (isset($user)) { ?>
                <ul class="nav navbar-nav">
                    <li
                    <?php
                    if (isset($url) && $url == base_url() . "user") {
                        echo " class=\"active\"";
                    }
                    ?>
                        ><a href="/user">I miei dati</a></li>
                    <li
                    <?php
                    if (isset($url) && $url == base_url() . "user/routes") {
                        echo " class=\"active\"";
                    }
                    ?>
                        ><a href="/user/routes">I miei tragitti</a></li>
                </ul>
            <?php } ?>
            <ul class="nav navbar-nav navbar-right">
                <?php if (isset($user)) { ?>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Ciao <?php echo $user->name; ?> <?php echo $user->surname; ?></a>
                        <ul class="dropdown-menu">
                            <li><a href="#" onclick="doLogoff()">Esci</a></li>
                        </ul>
                    </li>
                <?php } else { ?>
                    <!-- Button trigger modal -->
                    <?php $this->load->view('login'); ?>
                    <a data-toggle="modal" href="#loginModal" data-backdrop="static"><p class="text-right" style="padding-right: 30px">Accedi</p></a>
                <?php } ?>
            </ul>
        </div><!-- /.navbar-collapse -->
    </nav>
    <script type="text/javascript">
        function doLogoff()
        {
            $.getJSON("<?php echo base_url() ?>login/dologoff",
                    function(data) {
                        window.location.href = "<?php echo base_url() ?>";
                    });
        }
    </script>
</div>