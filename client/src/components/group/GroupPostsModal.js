import React from 'react';
import '../../styles/components/modal.scss';


const GroupPostsModal = ({ show, children }) => {
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

export default GroupPostsModal;
