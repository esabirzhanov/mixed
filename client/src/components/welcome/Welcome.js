import React from 'react';
import '../../styles/components/welcome.scss';

const Welcome = () => {
    return (
        <div>
            <div className="welcome">
                <h1>Welcome to React Powered Outstanding Bands App!</h1>
                  <ul>
                      <li>Building a stupid mixed app</li>
                      <li>Learning about the fundamentals of React</li>
                      <li>Building React apps with modern JavaScript (ES2015 and beyond)</li>
                      <li>Utilizing server-side rendering</li>
                      <li>Testing React applications</li>
                      <li>Implementing a Redux application architecture</li>
                  </ul>

              </div>
        </div>
    );
};

export default Welcome;
