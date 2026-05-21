'use client';
import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger' | 'bin';
  size?: 'sm' | 'md' | 'lg' | 'xl';
  leftIcon?: React.ReactNode;
  loading?: boolean;
}

export default function Button({ variant = 'primary', size = 'md', leftIcon, loading, children, className = '', ...rest }: ButtonProps) {
  const isDisabled = loading || rest.disabled;
  return (
    <button 
      className={`bm-btn bm-btn-${variant} bm-btn-${size} ${className} ${loading ? 'loading' : ''}`} 
      disabled={isDisabled}
      {...rest}
    >
      {loading ? (
        <span className="bm-spinner" style={{ marginRight: 8 }}>⌛</span>
      ) : leftIcon}
      {children}
    </button>
  );
}
