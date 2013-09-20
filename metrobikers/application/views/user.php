<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="row">
            <div class="col-md-12">
                <?php $this->load->view('templates/visualheader'); ?>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
            <h3>I miei dati</h3>
                email: <?php echo $user->mail; ?><br/>
                name: <?php echo $user->name; ?><br/>
                surname: <?php echo $user->surname; ?><br/>
                birthdate: <?php echo $user->birthdate; ?><br/>
		gender: <?php echo $user->gender; ?><br/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <?php $this->load->view('templates/footer'); ?>
            </div>
        </div>
    </body>
</html>
