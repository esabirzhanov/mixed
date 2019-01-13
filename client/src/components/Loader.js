import React from 'react';
import '../styles/global.scss';
import '../styles/components/loader.scss';

const Loader = () => (
    <div className="loader">
        <div className="ball-pulse-sync">
            <div />
            <div />
            <div />
        </div>
    </div>
);

export default Loader;
