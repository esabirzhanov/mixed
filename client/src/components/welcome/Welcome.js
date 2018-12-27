import React from 'react';
import '../../styles/components/welcome.scss';

const Welcome = () => {
    return (
        <div>
            <div className="welcome">
                <h1>Welcome to React Powered Outstanding Bands App!</h1>
                <p>
                    If you're here, you're probably reading{' '}
                    <a href="https://ifelse.io/book" target="_blank" rel="noopener noreferrer">
                        React in Action
                    </a>{' '}
                    from Manning Publications. This app is the example application that you'll build
                    as you go through the book. In React in Action, you'll learn:
                </p>
                <ul>
                    <li>Building a simple Music Bands social app</li>
                    <li>Learning about the fundamentals of React</li>
                    <li>Building React apps with modern JavaScript (ES2015 and beyond)</li>
                    <li>How React works (React in action covers through React 16 (fiber))</li>
                    <li>Implementing a routing system from scratch</li>
                    <li>Utilizing server-side rendering</li>
                    <li>Testing React applications</li>
                    <li>Implementing a Redux application architecture</li>
                </ul>
              </div>
        </div>
    );
};

export default Welcome;
