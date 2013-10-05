<?php $this->load->view('templates/redirectnotlogged'); ?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="container">

            <?php
	    $data['url'] = base_url()."user/tracks";
	    $this->load->view('templates/navigationbar', $data);
	    ?>

            <div class="row body">
		<?php
		$data = isset($routes) ? array('routes'=> $routes) : array();
		$this->load->view('map', $data);
		?>
            </div>

            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>
