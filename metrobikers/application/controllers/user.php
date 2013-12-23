<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class User extends MY_Controller {

    public function index() {
        if (!$this->validate_login())
            return;
        $data = array();

        $this->load_view('user', "I miei dati", $data);
    }

    public function routes() {
        if (!$this->validate_login())
            return;
        $this->load->model("Route_model");
        $this->Route_model->userid = $this->user->id;
        $routes = $this->Route_model->get_routes('0000-00-00 00:00:00');
        $data = array();
        $data['routes'] = $routes;

        $this->load_view('routes', 'I miei itinerari', $data);
    }

    public function preconnect($userid) {
        if (!$this->validate_login())
            return;
        if ($this->User_model->get_user_by_id($userid)) {
            $this->load->model('Validation_key_model');
            $this->Validation_key_model->create_key($this->user->id, VALIDATION_KEY_REASON_CONNECT);
            $data["user_contacted"] = $this->User_model;
            $data["user"] = $this->user;
            $data["validationkey"] = $this->Validation_key_model->validationkey;
            $view = $this->load->view('mail/contactusermailcontent', $data, TRUE);
            $this->send_mail($this->User_model->mail, lang("contact_submitted"), $view);

            $message = sprintf(lang("mail_for_contact"), $this->User_model->to_string());
            $this->load_my_ecommuters_view($message);
        }
    }

    public function disconnect($userid) {
        if (!$this->validate_login())
            return;
        if ($this->user->remove_linked_user($userid)) {
            $this->User_model->get_user_by_id($userid);
            $this->load_my_ecommuters_view(sprintf(lang("user_disconnected"), $this->User_model->to_string()));
        } else {
            $data['reason'] = "Si è verificato un errore eliminando l'utente dal gruppo.";
            $this->load_view("error", "Errore", $data);
        }
    }

    public function connect() {
        if (!$this->validate_login())
            return;
        $key = $this->input->get("userkey");
        $inviteduserid = $this->input->get("inviteduserid");
        if ($inviteduserid !== $this->user->id) {
            $data['reason'] = $this->user->to_string() . ", questo invito non è rivolto a te. Prova ad effettuare l'accesso utilizzando un altro utente.";
            $this->load_view("error", "ECOmmuter non corretto", $data);
            return;
        }
        $data['key'] = $key;
        if ($this->User_model->get_user_by_key($key)) {

            $this->load->model('Validation_key_model');
            $this->db->trans_begin();
            $this->user->insert_linked_user($this->User_model->id);
            $this->Validation_key_model->delete_key($key);
            $this->db->trans_commit();
            $data["user_contacted"] = $this->user;
            $data["user"] = $this->User_model;
            $view = $this->load->view('mail/useringroupmailcontent', $data, TRUE);
            $this->send_mail($this->User_model->mail, lang("contact_accepted"), $view);

            $this->load_my_ecommuters_view(sprintf(lang("user_connected"), $this->User_model->to_string()));
        } else {
            $data['reason'] = "La chiave di attivazione del collegamento non è presente nel nostro database.";
            $this->load_view("error", "Chiave di attivazione non valida", $data);
        }
    }

    function upload_photo() {
        $config['upload_path'] = './user_data/' . $this->user->id . '/';
        $config['allowed_types'] = 'gif|jpg|png';
        $config['max_size'] = '100';
        $config['max_width'] = '1024';
        $config['max_height'] = '768';
        $config['overwrite'] = TRUE;
        $config['file_name'] = 'photo';
        $this->load->library('upload', $config);

        if (!is_dir($this->upload->upload_path))
            mkdir($this->upload->upload_path);
        if (!$this->upload->do_upload()) {

            $data['reason'] = $this->upload->display_errors();
            $this->load_view("error", "Errore di caricamento della foto", $data);
        } else {
             $data = $this->upload->data();
           
            $files = glob($this->upload->upload_path .  '/photo*.{jpg,png,gif}', GLOB_BRACE);
            foreach ($files as $file) {
                $ext = '.' . pathinfo($file, PATHINFO_EXTENSION);  
                $ext1 = $data['file_ext'];
                if (strcmp($ext, $ext1) != 0)
                {
                    unlink($file);
                }
            }
            $this->index();
        }
    }

    private function load_my_ecommuters_view($startup_message) {
        $data = array();
        $data['linkedusers'] = $this->user->get_linked_users();
        if ($startup_message)
            $data['startup_message'] = $startup_message;
        $this->load_view('connections/my_ecommuters', 'Il mio gruppo', $data);
    }

    public function my_ecommuters() {
        if (!$this->validate_login())
            return;
        $this->load_my_ecommuters_view(NULL);
    }

    public function get_not_linked_users() {
        if ($this->user == NULL) {
            $response = array('users' => array());
        } else {
            $filter = $this->input->get("filter");
            $exact = $this->input->get("exact");
            $list = $this->user->get_not_linked_users($this->user->id, $filter, TRUE, $exact);
            if (!$exact && count($list) == 0)
                $list = $this->user->get_not_linked_users($this->user->id, $filter, FALSE, FALSE);
            $response = array('users' => $list);
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function update() {
        $response = (object) array('result' => FALSE);
        if ($this->user != NULL) {
            if ($this->user->update_user($this->input->post())) {

                $this->user->assign($this->input->post());
                set_user($this->user);
                $response->result = TRUE;
            }
        }

        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
