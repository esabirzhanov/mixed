import React from 'react';
import '../../styles/components/modal.scss';
import '../../styles/components/controlButtons.scss';

const Modal = ({ handleClose, show, children }) => {
    const showHideClassName = show ? "modal display-block" : "modal display-none";
    return (
        <div className={showHideClassName}>
          <div className="modal-backdrop"></div>
          <div className="modal-body">
            {children}
          </div>
        </div>
    );
};

export default Modal;
