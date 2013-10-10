<?php $this->load->view('templates/redirectnotlogged'); ?>
<div class="row header">
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <a class="navbar-brand" href="#">ECOmmuters</a>
    </div>
  
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse navbar-ex1-collapse">
      <ul class="nav navbar-nav">
        <li
        <?php
        if ($url == base_url()."user") {
          echo " class=\"active\"";
        }
        ?>
        ><a href="<?php echo base_url() ?>user">I miei dati</a></li>
        <li
        <?php
        if ($url == base_url()."user/tracks") {
          echo " class=\"active\"";
        }
        ?>
        ><a href="<?php echo base_url() ?>user/tracks">I miei tragitti</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Ciao <?php echo $user->name; ?> <?php echo $user->surname; ?></a>
          <ul class="dropdown-menu">
            <li><a href="#" onclick="doLogoff()">Esci</a></li>
          </ul>
        </li>
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