<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="container">

            <?php $this->load->view('templates/visualheader'); ?>

            <div class="row body">
                <div class="col-md-12">
                    <h3>I miei dati</h3>
                    email: <?php echo $user->mail; ?><br/>
                    name: <?php echo $user->name; ?><br/>
                    surname: <?php echo $user->surname; ?><br/>
                    birthdate: <?php echo $user->birthdate; ?><br/>
                    gender: <?php echo $user->gender; ?><br/>
                </div>
            </div>

            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>
