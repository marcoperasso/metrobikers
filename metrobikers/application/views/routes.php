<?php $this->load->view('templates/redirectnotlogged'); ?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="content">

            <?php
	    $data['url'] = "/user/routes";
	    $this->load->view('templates/visualheader', $data);
	    ?>

            <div class="body">
		<?php
		$data = isset($routes) ? array('routes'=> $routes) : array();
		$this->load->view('map', $data);
		?>
            </div>

            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>
